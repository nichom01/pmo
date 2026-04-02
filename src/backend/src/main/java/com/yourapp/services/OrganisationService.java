package com.yourapp.services;

import com.yourapp.dtos.CreateOrganisationRequest;
import com.yourapp.dtos.CreateTeamRequest;
import com.yourapp.entities.*;
import com.yourapp.dtos.OrganisationMemberResponse;
import com.yourapp.dtos.OrganisationResponse;
import com.yourapp.dtos.TeamResponse;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganisationService {
    private final OrganisationRepository organisationRepository;
    private final OrganisationMembershipRepository organisationMembershipRepository;
    private final TeamRepository teamRepository;

    public OrganisationService(
            OrganisationRepository organisationRepository,
            OrganisationMembershipRepository organisationMembershipRepository,
            TeamRepository teamRepository
    ) {
        this.organisationRepository = organisationRepository;
        this.organisationMembershipRepository = organisationMembershipRepository;
        this.teamRepository = teamRepository;
    }

    public OrganisationResponse getOrganisation(String orgSlug) {
        Organisation org = organisationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new NotFoundException("Organisation not found: " + orgSlug));
        return OrganisationResponse.from(org);
    }

    @Transactional(readOnly = true)
    public List<OrganisationMemberResponse> listMembers(String orgSlug) {
        Organisation org = organisationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new NotFoundException("Organisation not found: " + orgSlug));
        return organisationMembershipRepository.findByOrganisationId(org.getId())
                .stream()
                .map(OrganisationMemberResponse::from)
                .toList();
    }

    public List<TeamResponse> listTeams(String orgSlug) {
        Organisation org = organisationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new NotFoundException("Organisation not found: " + orgSlug));
        return teamRepository.findByOrganisationId(org.getId())
                .stream()
                .map(TeamResponse::from)
                .toList();
    }

    public OrganisationResponse createOrganisation(CreateOrganisationRequest request) {
        Organisation org = new Organisation();
        org.setName(request.name());
        org.setSlug(request.slug());
        return OrganisationResponse.from(organisationRepository.save(org));
    }

    public TeamResponse createTeam(String orgSlug, CreateTeamRequest request) {
        Organisation org = organisationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new NotFoundException("Organisation not found: " + orgSlug));
        Team team = new Team();
        team.setOrganisation(org);
        team.setName(request.name());
        team.setIdentifier(request.identifier());
        return TeamResponse.from(teamRepository.save(team));
    }

    public TeamResponse getTeam(String orgSlug, UUID teamId) {
        Organisation org = organisationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new NotFoundException("Organisation not found: " + orgSlug));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found: " + teamId));
        if (!team.getOrganisation().getId().equals(org.getId())) {
            throw new NotFoundException("Team not found in organisation: " + teamId);
        }
        return TeamResponse.from(team);
    }
}


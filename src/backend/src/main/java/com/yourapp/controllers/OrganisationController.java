package com.yourapp.controllers;

import com.yourapp.dtos.CreateOrganisationRequest;
import com.yourapp.dtos.CreateTeamRequest;
import com.yourapp.dtos.OrganisationMemberResponse;
import com.yourapp.dtos.OrganisationResponse;
import com.yourapp.dtos.TeamResponse;
import com.yourapp.exceptions.ForbiddenException;
import com.yourapp.services.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organisations")
public class OrganisationController {
    private final OrganisationService organisationService;

    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    @GetMapping("/{orgSlug}")
    public OrganisationResponse getOrganisation(@PathVariable String orgSlug) {
        return organisationService.getOrganisation(orgSlug);
    }

    @PostMapping
    public OrganisationResponse createOrganisation(
            @Valid @RequestBody CreateOrganisationRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        if (!"admin".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Only organisation admins can create organisations.");
        }
        return organisationService.createOrganisation(request);
    }

    @GetMapping("/{orgSlug}/members")
    public List<OrganisationMemberResponse> listMembers(@PathVariable String orgSlug) {
        return organisationService.listMembers(orgSlug);
    }

    @GetMapping("/{orgSlug}/teams")
    public List<TeamResponse> listTeams(@PathVariable String orgSlug) {
        return organisationService.listTeams(orgSlug);
    }

    @PostMapping("/{orgSlug}/teams")
    public TeamResponse createTeam(
            @PathVariable String orgSlug,
            @Valid @RequestBody CreateTeamRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        if (!"admin".equalsIgnoreCase(role) && !"owner".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Only organisation admins or team owners can create teams.");
        }
        return organisationService.createTeam(orgSlug, request);
    }

    @GetMapping("/{orgSlug}/teams/{teamId}")
    public TeamResponse getTeam(
            @PathVariable String orgSlug,
            @PathVariable UUID teamId
    ) {
        return organisationService.getTeam(orgSlug, teamId);
    }
}


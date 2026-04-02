package com.yourapp.security;

import com.yourapp.entities.Attachment;
import com.yourapp.entities.Comment;
import com.yourapp.entities.Cycle;
import com.yourapp.entities.Issue;
import com.yourapp.entities.Project;
import com.yourapp.entities.Team;
import com.yourapp.entities.User;
import com.yourapp.exceptions.ForbiddenException;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PermissionService {
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private OrganisationMembershipRepository organisationMembershipRepository;
    private TeamMembershipRepository teamMembershipRepository;
    private ProjectRepository projectRepository;
    private IssueRepository issueRepository;
    private CycleRepository cycleRepository;
    private CommentRepository commentRepository;
    private AttachmentRepository attachmentRepository;

    public PermissionService() {}

    @Autowired
    public PermissionService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            OrganisationMembershipRepository organisationMembershipRepository,
            TeamMembershipRepository teamMembershipRepository,
            ProjectRepository projectRepository,
            IssueRepository issueRepository,
            CycleRepository cycleRepository,
            CommentRepository commentRepository,
            AttachmentRepository attachmentRepository
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.organisationMembershipRepository = organisationMembershipRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.cycleRepository = cycleRepository;
        this.commentRepository = commentRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public boolean canMutateTeamResources(String role) {
        return "admin".equalsIgnoreCase(role) || "owner".equalsIgnoreCase(role) || "member".equalsIgnoreCase(role);
    }

    public void assertCanMutateTeamResources(String role) {
        if (!canMutateTeamResources(role)) {
            throw new ForbiddenException("You do not have permission to mutate this resource.");
        }
    }

    private User demoUser() {
        if (userRepository == null) {
            throw new IllegalStateException("PermissionService repositories are not configured.");
        }
        return userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo user not found"));
    }

    private boolean isOrgAdminOfTeam(User user, Team team) {
        UUID organisationId = team.getOrganisation().getId();
        return organisationMembershipRepository.findByOrganisationId(organisationId)
                .stream()
                .anyMatch(m ->
                        m.getUser().getId().equals(user.getId())
                                && "admin".equalsIgnoreCase(m.getRole())
                );
    }

    private boolean isAllowedTeamRole(User user, UUID teamId) {
        return teamMembershipRepository.findByTeamId(teamId)
                .stream()
                .anyMatch(m ->
                        m.getUser().getId().equals(user.getId())
                                && ("owner".equalsIgnoreCase(m.getRole()) || "member".equalsIgnoreCase(m.getRole()))
                );
    }

    @Transactional(readOnly = true)
    public boolean canMutateTeamResources(UUID teamId) {
        if (teamRepository == null) {
            throw new IllegalStateException("PermissionService repositories are not configured.");
        }
        User user = demoUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found: " + teamId));
        // Org admins implicitly get owner rights.
        if (isOrgAdminOfTeam(user, team)) {
            return true;
        }
        return isAllowedTeamRole(user, teamId);
    }

    @Transactional(readOnly = true)
    public void assertCanMutateTeamResources(UUID teamId) {
        if (!canMutateTeamResources(teamId)) {
            throw new ForbiddenException("You do not have permission to mutate this resource.");
        }
    }

    @Transactional(readOnly = true)
    public void assertCanMutateProjectResources(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
        assertCanMutateTeamResources(project.getTeam().getId());
    }

    @Transactional(readOnly = true)
    public void assertCanMutateIssueResources(UUID issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found: " + issueId));
        assertCanMutateTeamResources(issue.getTeam().getId());
    }

    @Transactional(readOnly = true)
    public void assertCanMutateCycleResources(UUID cycleId) {
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new NotFoundException("Cycle not found: " + cycleId));
        assertCanMutateTeamResources(cycle.getProject().getTeam().getId());
    }

    @Transactional(readOnly = true)
    public void assertCanMutateCommentResources(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));
        assertCanMutateIssueResources(comment.getIssue().getId());
    }

    @Transactional(readOnly = true)
    public void assertCanMutateAttachmentResources(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("Attachment not found: " + attachmentId));
        assertCanMutateIssueResources(attachment.getIssue().getId());
    }
}

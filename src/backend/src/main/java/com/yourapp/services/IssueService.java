package com.yourapp.services;

import com.yourapp.dtos.CreateIssueRequest;
import com.yourapp.dtos.IssueResponse;
import com.yourapp.dtos.PaginatedResponse;
import com.yourapp.entities.Issue;
import com.yourapp.entities.IssuePriority;
import com.yourapp.entities.Project;
import com.yourapp.entities.User;
import com.yourapp.entities.WorkflowState;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.CycleRepository;
import com.yourapp.repositories.IssueRepository;
import com.yourapp.repositories.ProjectRepository;
import com.yourapp.repositories.UserRepository;
import com.yourapp.repositories.WorkflowStateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final WorkflowStateRepository workflowStateRepository;
    private final UserRepository userRepository;
    private final CycleRepository cycleRepository;
    private final CursorService cursorService;

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            WorkflowStateRepository workflowStateRepository,
            UserRepository userRepository,
            CycleRepository cycleRepository,
            CursorService cursorService
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.workflowStateRepository = workflowStateRepository;
        this.userRepository = userRepository;
        this.cycleRepository = cycleRepository;
        this.cursorService = cursorService;
    }

    public Issue create(UUID projectId, CreateIssueRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
        WorkflowState state = workflowStateRepository.findById(request.workflowStateId())
                .orElseThrow(() -> new NotFoundException("Workflow state not found: " + request.workflowStateId()));
        if (!state.getTeam().getId().equals(project.getTeam().getId())) {
            throw new NotFoundException("Workflow state does not belong to this project's team.");
        }
        User reporter = userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo reporter user not found."));

        Issue issue = new Issue();
        issue.setProject(project);
        issue.setTeam(project.getTeam());
        issue.setWorkflowState(state);
        issue.setReporter(reporter);
        issue.setTitle(request.title());
        issue.setDescription(request.description());
        issue.setPriority(IssuePriority.none);
        issue.setSequenceNumber((int) issueRepository.countByProjectId(projectId) + 1);
        return issueRepository.save(issue);
    }

    public PaginatedResponse<IssueResponse> list(UUID projectId, String cursor, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        List<Issue> batch = cursor == null || cursor.isBlank()
                ? issueRepository.findTop101ByProjectIdOrderByCreatedAtDescIdDesc(projectId)
                : getWithCursor(projectId, cursor);

        List<Issue> paged = batch.stream().limit(safeLimit + 1L).toList();
        boolean hasMore = paged.size() > safeLimit;
        List<Issue> data = hasMore ? paged.subList(0, safeLimit) : paged;
        String nextCursor = null;
        if (hasMore && !data.isEmpty()) {
            Issue last = data.get(data.size() - 1);
            nextCursor = cursorService.encode(last.getCreatedAt(), last.getId());
        }
        return new PaginatedResponse<>(data.stream().map(IssueResponse::from).toList(), nextCursor, hasMore);
    }

    private List<Issue> getWithCursor(UUID projectId, String encodedCursor) {
        CursorService.Cursor cursor = cursorService.decode(encodedCursor);
        List<Issue> primary = issueRepository
                .findTop101ByProjectIdAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(projectId, cursor.createdAt());
        List<Issue> sameTimestamp = issueRepository
                .findTop101ByProjectIdAndCreatedAtEqualsAndIdLessThanOrderByCreatedAtDescIdDesc(projectId, cursor.createdAt(), cursor.id());
        return java.util.stream.Stream.concat(primary.stream(), sameTimestamp.stream())
                .sorted((a, b) -> {
                    int cmp = b.getCreatedAt().compareTo(a.getCreatedAt());
                    return cmp != 0 ? cmp : b.getId().compareTo(a.getId());
                })
                .limit(101)
                .toList();
    }

    public Issue assignCycle(UUID issueId, UUID cycleId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found: " + issueId));
        var cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new NotFoundException("Cycle not found: " + cycleId));
        if (!cycle.getProject().getId().equals(issue.getProject().getId())) {
            throw new NotFoundException("Cycle does not belong to issue project.");
        }
        issue.setCycle(cycle);
        return issueRepository.save(issue);
    }
}

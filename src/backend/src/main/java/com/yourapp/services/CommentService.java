package com.yourapp.services;

import com.yourapp.dtos.CreateCommentRequest;
import com.yourapp.entities.Comment;
import com.yourapp.entities.Issue;
import com.yourapp.entities.User;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.CommentRepository;
import com.yourapp.repositories.IssueRepository;
import com.yourapp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueActivityService issueActivityService;
    private final NotificationService notificationService;

    public CommentService(
            CommentRepository commentRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            IssueActivityService issueActivityService,
            NotificationService notificationService
    ) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.issueActivityService = issueActivityService;
        this.notificationService = notificationService;
    }

    public List<Comment> listForIssue(UUID issueId) {
        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId);
    }

    public Comment create(UUID issueId, CreateCommentRequest request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found: " + issueId));
        User author = userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo author not found"));
        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setAuthor(author);
        comment.setBody(request.body());
        Comment saved = commentRepository.save(comment);

        issueActivityService.record(issue, author, "comment_added", null, request.body());
        notificationService.create(author, author, issue, "issue_commented");
        return saved;
    }
}

package com.yourapp.services;

import com.yourapp.entities.Issue;
import com.yourapp.entities.IssueActivity;
import com.yourapp.entities.User;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.IssueActivityRepository;
import com.yourapp.repositories.IssueRepository;
import com.yourapp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IssueActivityService {
    private final IssueActivityRepository issueActivityRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public IssueActivityService(IssueActivityRepository issueActivityRepository, IssueRepository issueRepository, UserRepository userRepository) {
        this.issueActivityRepository = issueActivityRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    public void record(Issue issue, User actor, String type, String fromValue, String toValue) {
        IssueActivity activity = new IssueActivity();
        activity.setIssue(issue);
        activity.setActor(actor);
        activity.setType(type);
        activity.setFromValue(fromValue);
        activity.setToValue(toValue);
        issueActivityRepository.save(activity);
    }

    public User demoActor() {
        return userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo actor not found"));
    }

    public List<IssueActivity> listForIssue(UUID issueId) {
        issueRepository.findById(issueId).orElseThrow(() -> new NotFoundException("Issue not found: " + issueId));
        return issueActivityRepository.findByIssueIdOrderByCreatedAtDesc(issueId);
    }
}

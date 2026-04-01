package com.yourapp.controllers;

import com.yourapp.dtos.IssueActivityResponse;
import com.yourapp.services.IssueActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class IssueActivityController {
    private final IssueActivityService issueActivityService;

    public IssueActivityController(IssueActivityService issueActivityService) {
        this.issueActivityService = issueActivityService;
    }

    @GetMapping("/issues/{issueId}/activity")
    public List<IssueActivityResponse> list(@PathVariable UUID issueId) {
        return issueActivityService.listForIssue(issueId).stream().map(IssueActivityResponse::from).toList();
    }
}

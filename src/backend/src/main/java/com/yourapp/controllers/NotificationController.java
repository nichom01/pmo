package com.yourapp.controllers;

import com.yourapp.dtos.NotificationResponse;
import com.yourapp.services.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> list() {
        return notificationService.listForDemoUser().stream().map(NotificationResponse::from).toList();
    }

    @PostMapping("/{id}/read")
    public NotificationResponse read(@PathVariable UUID id) {
        return NotificationResponse.from(notificationService.markRead(id));
    }

    @PostMapping("/read-all")
    public Map<String, Integer> readAll() {
        return Map.of("updated", notificationService.markAllReadForDemoUser());
    }
}

package com.yourapp.controllers;

import com.yourapp.dtos.NotificationPreferenceResponse;
import com.yourapp.dtos.UpdateNotificationPreferenceRequest;
import com.yourapp.services.NotificationPreferenceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/notification-preferences")
public class NotificationPreferenceController {
    private final NotificationPreferenceService service;

    public NotificationPreferenceController(NotificationPreferenceService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationPreferenceResponse> list() {
        return service.list().stream().map(NotificationPreferenceResponse::from).toList();
    }

    @PatchMapping
    public NotificationPreferenceResponse update(@Valid @RequestBody UpdateNotificationPreferenceRequest request) {
        return NotificationPreferenceResponse.from(service.upsert(request));
    }
}

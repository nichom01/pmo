package com.yourapp.controllers;

import com.yourapp.dtos.ApiKeyResponse;
import com.yourapp.dtos.CreateApiKeyRequest;
import com.yourapp.dtos.CreateApiKeyResponse;
import com.yourapp.services.ApiKeyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/api-keys")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping
    public List<ApiKeyResponse> list() {
        return apiKeyService.listForDemoUser().stream().map(ApiKeyResponse::from).toList();
    }

    @PostMapping
    public CreateApiKeyResponse create(@Valid @RequestBody CreateApiKeyRequest request) {
        return apiKeyService.create(request);
    }

    @DeleteMapping("/{keyId}")
    public void delete(@PathVariable UUID keyId) {
        apiKeyService.delete(keyId);
    }
}

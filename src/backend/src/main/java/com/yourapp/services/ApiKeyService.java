package com.yourapp.services;

import com.yourapp.dtos.CreateApiKeyRequest;
import com.yourapp.dtos.CreateApiKeyResponse;
import com.yourapp.entities.ApiKey;
import com.yourapp.entities.User;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.ApiKeyRepository;
import com.yourapp.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ApiKeyService(ApiKeyRepository apiKeyRepository, UserRepository userRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
    }

    private User demoUser() {
        return userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo user not found"));
    }

    public List<ApiKey> listForDemoUser() {
        return apiKeyRepository.findByUserIdOrderByCreatedAtDesc(demoUser().getId());
    }

    public CreateApiKeyResponse create(CreateApiKeyRequest request) {
        User user = demoUser();
        String raw = "pmo_" + UUID.randomUUID().toString().replace("-", "");
        ApiKey key = new ApiKey();
        key.setUser(user);
        key.setLabel(request.label());
        key.setKeyHash(encoder.encode(raw));
        ApiKey saved = apiKeyRepository.save(key);
        return new CreateApiKeyResponse(com.yourapp.dtos.ApiKeyResponse.from(saved), raw);
    }

    public void delete(UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new NotFoundException("API key not found: " + keyId));
        apiKeyRepository.delete(key);
    }
}

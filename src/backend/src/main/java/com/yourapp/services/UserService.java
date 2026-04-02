package com.yourapp.services;

import com.yourapp.dtos.UpdateUserRequest;
import com.yourapp.entities.User;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User demoUser() {
        return userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo user not found"));
    }

    public User getMe() {
        return demoUser();
    }

    public User updateMe(UpdateUserRequest request) {
        User user = demoUser();
        user.setUsername(request.username());
        user.setAvatarUrl(request.avatarUrl());
        user.setTimezone(request.timezone());
        user.setUpdatedAt(OffsetDateTime.now());
        return userRepository.save(user);
    }
}


package com.yourapp.controllers;

import com.yourapp.dtos.UpdateUserRequest;
import com.yourapp.dtos.UserResponse;
import com.yourapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponse getMe() {
        return UserResponse.from(userService.getMe());
    }

    @PatchMapping
    public UserResponse updateMe(@Valid @RequestBody UpdateUserRequest request) {
        return UserResponse.from(userService.updateMe(request));
    }
}


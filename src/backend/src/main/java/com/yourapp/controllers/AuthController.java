package com.yourapp.controllers;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        ResponseCookie access = ResponseCookie.from("access_token", "local-access-token")
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();
        ResponseCookie refresh = ResponseCookie.from("refresh_token", "local-refresh-token")
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", access.toString())
                .header("Set-Cookie", refresh.toString())
                .body(Map.of("message", "logged_in"));
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh() {
        return Map.of("message", "refreshed");
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        ResponseCookie clearAccess = ResponseCookie.from("access_token", "")
                .httpOnly(true).path("/").maxAge(0).build();
        ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).path("/").maxAge(0).build();
        return ResponseEntity.ok()
                .header("Set-Cookie", clearAccess.toString())
                .header("Set-Cookie", clearRefresh.toString())
                .body(Map.of("message", "logged_out"));
    }
}

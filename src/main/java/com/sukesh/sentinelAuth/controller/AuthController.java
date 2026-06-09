package com.sukesh.sentinelAuth.controller;

import com.sukesh.sentinelAuth.dto.AuthRequest;
import com.sukesh.sentinelAuth.dto.AuthResponse;
import com.sukesh.sentinelAuth.dto.RefreshRequest;
import com.sukesh.sentinelAuth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }
    @PostMapping("/register")
    public String registerUser(@RequestBody AuthRequest authRequest)
    {
        return authService.registerUser(authRequest.getUsername(), authRequest.getPassword());
    }

    @GetMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest)
    {
        return authService.authenticateUser(authRequest.getUsername(), authRequest.getPassword());
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest refreshToken)
    {
        return authService.refresh(refreshToken.getRefreshToken());
    }
}

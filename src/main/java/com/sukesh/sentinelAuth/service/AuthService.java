package com.sukesh.sentinelAuth.service;

import com.sukesh.sentinelAuth.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String registerUser(String username,String password);
    ResponseEntity<AuthResponse> authenticateUser(String username, String password);
    ResponseEntity<AuthResponse> refresh(String token);
    String logoutUser(String token);
}

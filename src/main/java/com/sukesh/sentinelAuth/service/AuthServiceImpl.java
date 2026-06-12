package com.sukesh.sentinelAuth.service;

import com.sukesh.sentinelAuth.dto.AuthResponse;
import com.sukesh.sentinelAuth.entity.RefreshTokens;
import com.sukesh.sentinelAuth.entity.Users;
import com.sukesh.sentinelAuth.repository.RefreshTokensRepository;
import com.sukesh.sentinelAuth.repository.UsersRepository;
import com.sukesh.sentinelAuth.util.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokensRepository refreshTokensRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder,JwtUtils jwtUtils, RefreshTokensRepository refreshTokensRepository, AuthenticationManager authenticationManager)
    {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokensRepository = refreshTokensRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public String registerUser(String username,String password) {

        Optional<Users> user = usersRepository.findByName(username);
        if(user.isPresent())
        {
            throw new RuntimeException("user already exists");
        }
        Users newUser = new Users(username,passwordEncoder.encode(password), Instant.now());
        usersRepository.save(newUser);


        return "User registered";
    }

    @Override
    @Transactional
    public ResponseEntity<AuthResponse> authenticateUser(String username, String password)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username,password)
        );
        Optional<Users> user = usersRepository.findByName(username);
        String accessToken = jwtUtils.generateAccessToken(String.valueOf(user.get().getUserId()));
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(user.get().getUserId()));
        RefreshTokens token = new RefreshTokens(user.get(),jwtUtils.getJTI(refreshToken),jwtUtils.getExpirationTime(refreshToken));
        refreshTokensRepository.save(token);
        //user.get().getRefreshTokens().add(token);
        //usersRepository.save(user.get());
        AuthResponse authResponse = new AuthResponse(accessToken,refreshToken);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<AuthResponse> refresh(String token) {
        String tokenId = jwtUtils.getJTI(token);
        String userId  = jwtUtils.getSubject(token);

        RefreshTokens dbToken = refreshTokensRepository.findByToken(tokenId)
                .orElseThrow(() -> new SecurityException("Invalid Refresh Token"));

        if (!userId.equals(String.valueOf(dbToken.getUser().getUserId()))) {
            throw new SecurityException("Token ownership mismatch");
        }


        if (dbToken.isUsed()) {
            System.out.println("CRITICAL ALERT: Token replay attack detected for User ID: " + userId);
            refreshTokensRepository.revokeAllTokensByUserId(dbToken.getUser().getUserId());
            SecurityContextHolder.clearContext();

            throw new SecurityException("Security breach detected. All active sessions have been invalidated.");
        }


        if (jwtUtils.isExpired(token)) {
            System.out.println("Session expired naturally for User ID: " + userId);
            SecurityContextHolder.clearContext();
            throw new SecurityException("Session expired. Please log in again.");
        }


        dbToken.setUsed(true);
        refreshTokensRepository.save(dbToken);


        Users user = dbToken.getUser();

        String accessToken = jwtUtils.generateAccessToken(String.valueOf(user.getUserId()));
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(user.getUserId()));

        RefreshTokens newToken = new RefreshTokens(
                user,
                jwtUtils.getJTI(refreshToken),
                jwtUtils.getExpirationTime(refreshToken)
        );
        refreshTokensRepository.save(newToken);

        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @Transactional
    public String logoutUser(String token) {
        String tokenId = jwtUtils.getJTI(token);
        String userId  = jwtUtils.getSubject(token);
        RefreshTokens dbToken = refreshTokensRepository.findByToken(tokenId)
                .orElseThrow(() -> new SecurityException("Invalid Refresh Token"));

        if (!userId.equals(String.valueOf(dbToken.getUser().getUserId()))) {
            throw new SecurityException("Token ownership mismatch");
        }

        if (dbToken.isUsed()) {
            refreshTokensRepository.revokeAllTokensByUserId(dbToken.getUser().getUserId());
            SecurityContextHolder.clearContext();
            throw new SecurityException("Security breach detected. All active sessions have been invalidated.");
        }


        if (jwtUtils.isExpired(token)) {
            refreshTokensRepository.revokeAllTokensByUserId(dbToken.getUser().getUserId());
            SecurityContextHolder.clearContext();
            return "User Session Expired & Logged Out Successfully";
        }
        refreshTokensRepository.revokeAllTokensByUserId(dbToken.getUser().getUserId());
        SecurityContextHolder.clearContext();
        return "User Logout";
    }


}

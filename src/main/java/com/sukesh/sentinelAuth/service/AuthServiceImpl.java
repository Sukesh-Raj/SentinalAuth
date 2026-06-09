package com.sukesh.sentinelAuth.service;

import com.sukesh.sentinelAuth.dto.AuthResponse;
import com.sukesh.sentinelAuth.entity.RefreshTokens;
import com.sukesh.sentinelAuth.entity.Users;
import com.sukesh.sentinelAuth.repository.RefreshTokensRepository;
import com.sukesh.sentinelAuth.repository.UsersRepository;
import com.sukesh.sentinelAuth.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public ResponseEntity<AuthResponse> authenticateUser(String username, String password)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username,password)
        );
        Optional<Users> user = usersRepository.findByName(username);
        String accessToken = jwtUtils.generateAccessToken(String.valueOf(user.get().getUserId()));
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(user.get().getUserId()));
        RefreshTokens token = new RefreshTokens(user.get(),refreshToken,jwtUtils.getExpirationTime(refreshToken));
        refreshTokensRepository.save(token);
        user.get().getRefreshTokens().add(token);
        usersRepository.save(user.get());
        AuthResponse authResponse = new AuthResponse(accessToken,refreshToken);
        return ResponseEntity.ok(authResponse);
    }


}

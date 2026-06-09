package com.sukesh.sentinelAuth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private final long accessTokenExpirationTime = 1000*60*15;
    private final long refreshTokenExpirationTime = 1000*60*60*24;
    private final String SECRET ="the current hero era is end now and the beginnning of new era starts now";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    public String generateAccessToken(String userId)
    {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId)
    {
        String tokenId = UUID.randomUUID().toString();
        return Jwts.builder()
                .setId(tokenId)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String getJTI(String token)
    {
        return extractClaims(token).getId();
    }
    public Instant getExpirationTime(String token)
    {
        return extractClaims(token).getExpiration().toInstant();
    }

    public String getSubject(String token)
    {
        return extractClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails, String userId)
    {
        return userId.equals(userDetails.getUsername()) && !isExpired(token);
    }

    public boolean isExpired(String token)
    {
        return getExpirationTime(token).isBefore(Instant.now());
    }
}

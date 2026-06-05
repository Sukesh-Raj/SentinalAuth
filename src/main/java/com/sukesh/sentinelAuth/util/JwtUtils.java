package com.sukesh.sentinelAuth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {

    private final long accessTokenExpirationTime = 1000*60*15;
    private final long refreshTokenExpirationTime = 1000*60*60*24;
    private final String SECRET ="the current hero era is end now and the beginnning of new era starts now";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    public String generateAccessToken(String user_id)
    {
        return Jwts.builder()
                .setSubject(user_id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String user_id)
    {
        return Jwts.builder()
                .setSubject(user_id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


}

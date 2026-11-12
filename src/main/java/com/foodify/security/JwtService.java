package com.foodify.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(
                    io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        java.util.Base64.getDecoder().decode(secret)
                    ),
                    SignatureAlgorithm.HS256
                )
                .compact();
    }
    
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(
                    java.util.Base64.getDecoder().decode(secret)
                )
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email);
    }
}
package com.example.moveSmart.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtTokenUtils(@Value("${jwt.secret}") String jwtSecret) {
        // Decode the base64-encoded key
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(60 * 60 * 24 * 7))); // 1 week expiration

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(this.signingKey, SignatureAlgorithm.HS384)
                .compact();
    }

    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT", e);
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }
}

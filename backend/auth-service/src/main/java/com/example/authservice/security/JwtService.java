package com.example.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")      // <-- store a Base64-encoded 64-byte key in application.yml
    private String base64Secret;

    @Value("${app.jwt.expires-in}")  // e.g., 3600000 (1 hour)
    private long jwtExpiresIn;

    private SecretKey key() {
        // IMPORTANT: decode Base64; do NOT use getBytes()
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(jwtExpiresIn);

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(extraClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                // IMPORTANT: pin the algorithm to HS512
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(key())      // same decoded key used for signing
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}

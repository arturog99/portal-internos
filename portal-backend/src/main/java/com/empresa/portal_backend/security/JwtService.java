package com.empresa.portal_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

// Servicio para crear y validar tokens JWT.
// Maneja dos tipos de token:
//  - "access": token de acceso completo tras autenticarse (y pasar 2FA si aplica)
//  - "2fa":    token temporal corto entre el login y la verificacion del codigo 2FA
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long twoFaExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long accessExpirationMs,
            @Value("${app.jwt.twofa-expiration-ms}") long twoFaExpirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpirationMs = accessExpirationMs;
        this.twoFaExpirationMs = twoFaExpirationMs;
    }

    // Token de acceso definitivo
    public String generateAccessToken(String username, String role) {
        return buildToken(username, role, "access", accessExpirationMs);
    }

    // Token temporal previo a la verificacion 2FA
    public String generateTwoFactorToken(String username, String role) {
        return buildToken(username, role, "2fa", twoFaExpirationMs);
    }

    private String buildToken(String username, String role, String type, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    public String extractRole(String token) {
        return parse(token).getPayload().get("role", String.class);
    }

    public String extractType(String token) {
        return parse(token).getPayload().get("type", String.class);
    }

    public boolean isAccessToken(String token) {
        try {
            return "access".equals(extractType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTwoFactorToken(String token) {
        try {
            return "2fa".equals(extractType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}

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

/**
 * Servicio para la creación y validación de tokens JWT.
 * 
 * Este servicio maneja dos tipos de tokens:
 * - Access Token: Token de acceso completo tras autenticarse (y pasar 2FA si aplica)
 * - 2FA Token: Token temporal corto entre el login y la verificación del código 2FA
 * 
 * Los tokens contienen las siguientes claims:
 * - subject: nombre de usuario
 * - role: rol del usuario (ADMIN, TECNICO, VISITANTE)
 * - type: tipo de token ("access" o "2fa")
 * - issuedAt: fecha de emisión
 * - expiration: fecha de expiración
 */
@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_2FA = "2fa";

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long twoFaExpirationMs;

    /**
     * Constructor del servicio JWT.
     *
     * @param secret Clave secreta codificada en Base64 para firmar tokens
     * @param accessExpirationMs Tiempo de expiración del token de acceso en milisegundos
     * @param twoFaExpirationMs Tiempo de expiración del token 2FA en milisegundos
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long accessExpirationMs,
            @Value("${app.jwt.twofa-expiration-ms}") long twoFaExpirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpirationMs = accessExpirationMs;
        this.twoFaExpirationMs = twoFaExpirationMs;
    }

    /**
     * Genera un token de acceso definitivo para el usuario.
     * 
     * Este token se utiliza después de una autenticación exitosa (incluyendo 2FA si aplica)
     * y permite el acceso a los endpoints protegidos según el rol del usuario.
     *
     * @param username Nombre de usuario
     * @param role Rol del usuario (ADMIN, TECNICO, VISITANTE)
     * @return Token JWT de acceso firmado
     */
    public String generateAccessToken(String username, String role) {
        return buildToken(username, role, TOKEN_TYPE_ACCESS, accessExpirationMs);
    }

    /**
     * Genera un token temporal para el proceso de autenticación de dos factores.
     * 
     * Este token tiene una vida útil corta y solo sirve para verificar el código 2FA.
     * No permite acceso a endpoints protegidos.
     *
     * @param username Nombre de usuario
     * @param role Rol del usuario (ADMIN, TECNICO, VISITANTE)
     * @return Token JWT temporal firmado
     */
    public String generateTwoFactorToken(String username, String role) {
        return buildToken(username, role, TOKEN_TYPE_2FA, twoFaExpirationMs);
    }

    /**
     * Construye un token JWT con los parámetros especificados.
     *
     * @param username Nombre de usuario (subject del token)
     * @param role Rol del usuario
     * @param type Tipo de token (access o 2fa)
     * @param expirationMs Tiempo de expiración en milisegundos
     * @return Token JWT firmado
     */
    private String buildToken(String username, String role, String type, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extrae el nombre de usuario del token.
     *
     * @param token Token JWT a analizar
     * @return Nombre de usuario contenido en el token
     */
    public String extractUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    /**
     * Extrae el rol del usuario del token.
     *
     * @param token Token JWT a analizar
     * @return Rol del usuario (ADMIN, TECNICO, VISITANTE)
     */
    public String extractRole(String token) {
        return parse(token).getPayload().get(CLAIM_ROLE, String.class);
    }

    /**
     * Extrae el tipo de token del token.
     *
     * @param token Token JWT a analizar
     * @return Tipo de token ("access" o "2fa")
     */
    public String extractType(String token) {
        return parse(token).getPayload().get(CLAIM_TYPE, String.class);
    }

    /**
     * Verifica si el token es de tipo "access" (token de acceso completo).
     *
     * @param token Token JWT a verificar
     * @return true si es un token de acceso válido, false en caso contrario
     */
    public boolean isAccessToken(String token) {
        try {
            return TOKEN_TYPE_ACCESS.equals(extractType(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el token es de tipo "2fa" (token temporal para verificación 2FA).
     *
     * @param token Token JWT a verificar
     * @return true si es un token 2FA válido, false en caso contrario
     */
    public boolean isTwoFactorToken(String token) {
        try {
            return TOKEN_TYPE_2FA.equals(extractType(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el token es válido (firma correcta y no expirado).
     *
     * @param token Token JWT a verificar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parsea y valida el token JWT.
     *
     * @param token Token JWT a parsear
     * @return Claims del token parseado
     * @throws Exception Si el token es inválido o está expirado
     */
    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}

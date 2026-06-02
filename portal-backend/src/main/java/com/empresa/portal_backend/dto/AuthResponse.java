package com.empresa.portal_backend.dto;

// Respuesta de autenticacion.
// - Si twoFactorRequired = true: se devuelve tempToken (para el paso de verificacion 2FA) y token = null.
// - Si twoFactorRequired = false: se devuelve token (JWT de acceso) directamente.
public record AuthResponse(
        boolean twoFactorRequired,
        String token,
        String tempToken,
        String username,
        String role
) {
    public static AuthResponse requires2fa(String tempToken, String username, String role) {
        return new AuthResponse(true, null, tempToken, username, role);
    }

    public static AuthResponse authenticated(String token, String username, String role) {
        return new AuthResponse(false, token, null, username, role);
    }
}

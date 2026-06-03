package com.empresa.portal_backend.dto;

/**
 * Respuesta de autenticación.
 * 
 * Si twoFactorRequired = true: se devuelve tempToken (para el paso de verificación 2FA) y token = null.
 * Si twoFactorRequired = false: se devuelve token (JWT de acceso) directamente.
 */
public record AuthResponse(
        /** Indica si se requiere verificación de dos factores */
        boolean twoFactorRequired,
        /** Token JWT de acceso (null si se requiere 2FA) */
        String token,
        /** Token temporal para verificación 2FA (null si no se requiere 2FA) */
        String tempToken,
        /** Nombre de usuario autenticado */
        String username,
        /** Rol del usuario autenticado */
        String role
) {
    /**
     * Crea una respuesta indicando que se requiere verificación 2FA.
     *
     * @param tempToken Token temporal para el paso de verificación 2FA
     * @param username Nombre de usuario
     * @param role Rol del usuario
     * @return AuthResponse con twoFactorRequired = true
     */
    public static AuthResponse requires2fa(String tempToken, String username, String role) {
        return new AuthResponse(true, null, tempToken, username, role);
    }

    /**
     * Crea una respuesta de autenticación completada.
     *
     * @param token Token JWT de acceso
     * @param username Nombre de usuario
     * @param role Rol del usuario
     * @return AuthResponse con twoFactorRequired = false
     */
    public static AuthResponse authenticated(String token, String username, String role) {
        return new AuthResponse(false, token, null, username, role);
    }
}

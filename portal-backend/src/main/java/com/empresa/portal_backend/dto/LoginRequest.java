package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud de login con credenciales de usuario.
 */
public record LoginRequest(
        /** Nombre de usuario para el login */
        @NotBlank String username,
        /** Contraseña del usuario */
        @NotBlank String password
) {
}

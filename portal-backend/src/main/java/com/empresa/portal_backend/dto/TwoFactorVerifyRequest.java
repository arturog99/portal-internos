package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud para verificar el código 2FA y obtener el token de acceso.
 */
public record TwoFactorVerifyRequest(
        /** Token temporal devuelto en el paso 1 del login */
        @NotBlank String tempToken,
        /** Código TOTP de 6 dígitos generado por la app de autenticación */
        @NotBlank String code
) {
}

package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud para activar el 2FA tras verificar el primer código.
 */
public record TwoFactorEnableRequest(
        /** Código TOTP de 6 dígitos generado por la app de autenticación */
        @NotBlank String code
) {
}

package com.empresa.portal_backend.dto;

import com.empresa.portal_backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Solicitud para crear o actualizar un usuario.
 */
public record UserRequest(
        /** Nombre de usuario único */
        @NotBlank String username,
        /** Email único del usuario */
        @NotBlank @Email String email,
        /** Contraseña del usuario (se codificará con BCrypt) */
        @NotBlank String password,
        /** Rol del usuario en el sistema */
        @NotNull Role role
) {
}

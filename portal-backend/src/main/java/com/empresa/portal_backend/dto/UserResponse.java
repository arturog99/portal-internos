package com.empresa.portal_backend.dto;

import com.empresa.portal_backend.model.Role;
import com.empresa.portal_backend.model.User;

/**
 * Respuesta con los datos de un usuario.
 */
public record UserResponse(
        /** Identificador único del usuario */
        Long id,
        /** Nombre de usuario */
        String username,
        /** Email del usuario */
        String email,
        /** Rol del usuario en el sistema */
        Role role,
        /** Indica si el usuario tiene 2FA habilitado */
        boolean totpEnabled,
        /** Identificador del certificado digital X.509 asociado */
        String certificateId,
        /** Indica si la cuenta del usuario está activa */
        boolean enabled
) {
    /**
     * Crea una UserResponse desde una entidad User.
     *
     * @param u Entidad User
     * @return UserResponse con los datos del usuario
     */
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(),
                u.isTotpEnabled(), u.getCertificateId(), u.isEnabled());
    }
}

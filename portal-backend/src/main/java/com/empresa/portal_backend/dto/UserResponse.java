package com.empresa.portal_backend.dto;

import com.empresa.portal_backend.model.Role;
import com.empresa.portal_backend.model.User;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        boolean totpEnabled,
        String certificateId,
        boolean enabled
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(),
                u.isTotpEnabled(), u.getCertificateId(), u.isEnabled());
    }
}

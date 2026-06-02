package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record TwoFactorVerifyRequest(
        @NotBlank String tempToken,
        @NotBlank String code
) {
}

package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record TwoFactorEnableRequest(
        @NotBlank String code
) {
}

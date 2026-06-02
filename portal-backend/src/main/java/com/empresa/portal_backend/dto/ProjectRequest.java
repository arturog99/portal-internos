package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProjectRequest(
        @NotBlank String name,
        String description,
        @NotNull List<String> tags,
        @NotBlank String status
) {
}

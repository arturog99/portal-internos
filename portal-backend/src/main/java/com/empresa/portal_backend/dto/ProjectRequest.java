package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Solicitud para crear o actualizar un proyecto.
 */
public record ProjectRequest(
        /** Nombre del proyecto */
        @NotBlank String name,
        /** Descripción detallada del proyecto */
        String description,
        /** Lista de tecnologías utilizadas (tags) */
        @NotNull List<String> tags,
        /** Estado actual del proyecto */
        @NotBlank String status
) {
}

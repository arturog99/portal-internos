package com.empresa.portal_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud para actualizar el estado de un proyecto.
 */
public record UpdateStatusRequest(
        /** Nuevo estado del proyecto */
        @NotBlank String status
) {
}

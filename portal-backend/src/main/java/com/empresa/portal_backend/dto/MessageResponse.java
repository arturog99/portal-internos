package com.empresa.portal_backend.dto;

/**
 * Respuesta genérica con un mensaje de texto.
 */
public record MessageResponse(
        /** Mensaje de respuesta */
        String message
) {
}

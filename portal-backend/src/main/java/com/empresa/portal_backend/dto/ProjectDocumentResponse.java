package com.empresa.portal_backend.dto;

import com.empresa.portal_backend.model.ProjectDocument;

import java.time.Instant;

/**
 * Respuesta con los metadatos de un documento de avance de un proyecto.
 */
public record ProjectDocumentResponse(
        /** Identificador único del documento */
        Long id,
        /** Nombre original del archivo */
        String filename,
        /** Tipo MIME del archivo */
        String contentType,
        /** Tamaño del archivo en bytes */
        long size,
        /** Usuario que subió el documento */
        String uploadedBy,
        /** Fecha y hora de la subida */
        Instant uploadedAt
) {
    /**
     * Crea una ProjectDocumentResponse desde una entidad ProjectDocument.
     *
     * @param d Entidad ProjectDocument
     * @return ProjectDocumentResponse con los metadatos del documento
     */
    public static ProjectDocumentResponse from(ProjectDocument d) {
        return new ProjectDocumentResponse(
                d.getId(), d.getFilename(), d.getContentType(),
                d.getSize(), d.getUploadedBy(), d.getUploadedAt());
    }
}

package com.empresa.portal_backend.dto;

import com.empresa.portal_backend.model.Project;

import java.util.List;

/**
 * Respuesta con los datos de un proyecto.
 */
public record ProjectResponse(
        /** Identificador único del proyecto */
        Long id,
        /** Nombre del proyecto */
        String name,
        /** Descripción detallada del proyecto */
        String description,
        /** Lista de tecnologías utilizadas (tags) */
        List<String> tags,
        /** Estado actual del proyecto */
        String status
) {
    /**
     * Crea una ProjectResponse desde una entidad Project.
     *
     * @param p Entidad Project
     * @return ProjectResponse con los datos del proyecto
     */
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getTags(), p.getStatus());
    }
}

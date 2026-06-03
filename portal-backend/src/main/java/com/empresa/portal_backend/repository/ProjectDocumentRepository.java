package com.empresa.portal_backend.repository;

import com.empresa.portal_backend.model.ProjectDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para la entidad ProjectDocument.
 *
 * Proporciona acceso a los documentos de avance asociados a un proyecto.
 */
public interface ProjectDocumentRepository extends JpaRepository<ProjectDocument, Long> {

    /**
     * Obtiene los documentos de un proyecto ordenados por fecha de subida descendente.
     *
     * @param projectId ID del proyecto
     * @return Lista de documentos del proyecto
     */
    List<ProjectDocument> findByProjectIdOrderByUploadedAtDesc(Long projectId);
}

package com.empresa.portal_backend.repository;

import com.empresa.portal_backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad Project.
 * 
 * Proporciona métodos CRUD estándar para la gestión de proyectos.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
}

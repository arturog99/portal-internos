package com.empresa.portal_backend.controller;

import com.empresa.portal_backend.dto.ProjectRequest;
import com.empresa.portal_backend.dto.ProjectResponse;
import com.empresa.portal_backend.dto.UpdateStatusRequest;
import com.empresa.portal_backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de proyectos del sistema.
 * 
 * Proporciona operaciones CRUD para proyectos.
 * El control de acceso por rol se aplica en SecurityConfig:
 * - GET: ADMIN, TECNICO, VISITANTE
 * - POST: ADMIN
 * - PUT: ADMIN, TECNICO
 * - PATCH: ADMIN, TECNICO (cambio de estado)
 * - DELETE: ADMIN
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Constructor del controlador de proyectos.
     *
     * @param projectService Servicio de gestión de proyectos
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Obtiene todos los proyectos del sistema.
     *
     * @return Lista de proyectos
     */
    @GetMapping
    public List<ProjectResponse> getAll() {
        return projectService.findAll().stream().map(ProjectResponse::from).toList();
    }

    /**
     * Busca un proyecto por su ID.
     *
     * @param id ID del proyecto
     * @return Proyecto encontrado
     */
    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return ProjectResponse.from(projectService.findById(id));
    }

    /**
     * Crea un nuevo proyecto en el sistema.
     *
     * @param request Datos del proyecto a crear
     * @return Proyecto creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse created = ProjectResponse.from(projectService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Actualiza los datos de un proyecto existente.
     *
     * @param id ID del proyecto a actualizar
     * @param request Nuevos datos del proyecto
     * @return Proyecto actualizado
     */
    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ProjectResponse.from(projectService.update(id, request));
    }

    /**
     * Actualiza únicamente el estado de un proyecto.
     *
     * @param id ID del proyecto
     * @param request Nuevo estado del proyecto
     * @return Proyecto con el estado actualizado
     */
    @PatchMapping("/{id}/status")
    public ProjectResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ProjectResponse.from(projectService.updateStatus(id, request.status()));
    }

    /**
     * Elimina un proyecto del sistema.
     *
     * @param id ID del proyecto a eliminar
     * @return Respuesta vacía con estado HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

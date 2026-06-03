package com.empresa.portal_backend.service;

import com.empresa.portal_backend.dto.ProjectRequest;
import com.empresa.portal_backend.model.Project;
import com.empresa.portal_backend.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de proyectos del sistema.
 * 
 * Proporciona operaciones CRUD para proyectos, incluyendo actualización
 * de estado y gestión de etiquetas tecnológicas.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * Constructor del servicio de proyectos.
     *
     * @param projectRepository Repositorio de proyectos
     */
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Obtiene todos los proyectos del sistema.
     *
     * @return Lista de todos los proyectos
     */
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    /**
     * Busca un proyecto por su ID.
     *
     * @param id ID del proyecto
     * @return Proyecto encontrado
     * @throws EntityNotFoundException Si no existe un proyecto con ese ID
     */
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + id));
    }

    /**
     * Crea un nuevo proyecto en el sistema.
     *
     * @param request Datos del proyecto a crear
     * @return Proyecto creado
     */
    @Transactional
    public Project create(ProjectRequest request) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .tags(new ArrayList<>(request.tags()))
                .status(request.status())
                .build();
        return projectRepository.save(project);
    }

    /**
     * Actualiza los datos de un proyecto existente.
     *
     * @param id ID del proyecto a actualizar
     * @param request Nuevos datos del proyecto
     * @return Proyecto actualizado
     * @throws EntityNotFoundException Si no existe un proyecto con ese ID
     */
    @Transactional
    public Project update(Long id, ProjectRequest request) {
        Project project = findById(id);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setTags(new ArrayList<>(request.tags()));
        project.setStatus(request.status());
        return projectRepository.save(project);
    }

    /**
     * Actualiza únicamente el estado de un proyecto.
     * 
     * Este método permite cambiar el estado (Producción, Mantenimiento, En Desarrollo, etc.)
     * sin modificar otros campos del proyecto.
     *
     * @param id ID del proyecto
     * @param status Nuevo estado del proyecto
     * @return Proyecto con el estado actualizado
     * @throws EntityNotFoundException Si no existe un proyecto con ese ID
     */
    @Transactional
    public Project updateStatus(Long id, String status) {
        Project project = findById(id);
        project.setStatus(status);
        return projectRepository.save(project);
    }

    /**
     * Elimina un proyecto del sistema.
     *
     * @param id ID del proyecto a eliminar
     * @throws EntityNotFoundException Si no existe un proyecto con ese ID
     */
    @Transactional
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Proyecto no encontrado: " + id);
        }
        projectRepository.deleteById(id);
    }
}

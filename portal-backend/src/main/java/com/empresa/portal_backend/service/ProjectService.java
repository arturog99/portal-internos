package com.empresa.portal_backend.service;

import com.empresa.portal_backend.dto.ProjectRequest;
import com.empresa.portal_backend.model.Project;
import com.empresa.portal_backend.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + id));
    }

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

    @Transactional
    public Project update(Long id, ProjectRequest request) {
        Project project = findById(id);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setTags(new ArrayList<>(request.tags()));
        project.setStatus(request.status());
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateStatus(Long id, String status) {
        Project project = findById(id);
        project.setStatus(status);
        return projectRepository.save(project);
    }

    @Transactional
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Proyecto no encontrado: " + id);
        }
        projectRepository.deleteById(id);
    }
}

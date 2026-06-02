package com.empresa.portal_backend.repository;

import com.empresa.portal_backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}

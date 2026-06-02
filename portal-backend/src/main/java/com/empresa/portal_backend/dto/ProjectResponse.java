package com.empresa.portal_backend.dto;

import com.empresa.portal_backend.model.Project;

import java.util.List;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        List<String> tags,
        String status
) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getTags(), p.getStatus());
    }
}

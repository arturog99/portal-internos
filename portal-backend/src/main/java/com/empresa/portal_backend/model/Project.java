package com.empresa.portal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un proyecto del catálogo interno.
 * 
 * Esta entidad almacena información sobre proyectos de la empresa,
 * incluyendo nombre, descripción, tecnologías utilizadas y estado actual.
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    /**
     * Identificador único del proyecto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del proyecto.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Descripción detallada del proyecto (máximo 1000 caracteres).
     */
    @Column(length = 1000)
    private String description;

    /**
     * Lista de tecnologías utilizadas en el proyecto (tags).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    /**
     * Estado actual del proyecto (ej. "Producción", "En Desarrollo", "Mantenimiento").
     */
    @Column(nullable = false)
    private String status;
}

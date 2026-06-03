package com.empresa.portal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Documento de avance asociado a un proyecto.
 *
 * Almacena los metadatos del archivo (nombre original, tipo, tamaño, quién y
 * cuándo lo subió). El contenido binario se guarda en disco; aquí solo se
 * referencia mediante {@code storedFilename}.
 */
@Entity
@Table(name = "project_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDocument {

    /**
     * Identificador único del documento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre original del archivo subido por el usuario.
     */
    @Column(nullable = false)
    private String filename;

    /**
     * Nombre único con el que se almacena el archivo en disco (evita colisiones).
     */
    @Column(nullable = false)
    private String storedFilename;

    /**
     * Tipo MIME del archivo (ej. application/pdf).
     */
    private String contentType;

    /**
     * Tamaño del archivo en bytes.
     */
    private long size;

    /**
     * Nombre de usuario que subió el documento.
     */
    private String uploadedBy;

    /**
     * Fecha y hora de la subida.
     */
    @Column(nullable = false)
    private Instant uploadedAt;

    /**
     * Proyecto al que pertenece el documento.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}

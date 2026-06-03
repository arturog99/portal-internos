package com.empresa.portal_backend.controller;

import com.empresa.portal_backend.dto.ProjectDocumentResponse;
import com.empresa.portal_backend.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para la gestión de documentos de avance de proyectos.
 *
 * Proporciona endpoints para subir, listar, descargar y borrar documentos.
 * El control de acceso por rol se aplica en SecurityConfig:
 * - GET /api/projects/{id}/documents: ADMIN, TECNICO
 * - POST /api/projects/{id}/documents: ADMIN, TECNICO
 * - GET /api/projects/{id}/documents/{docId}/download: ADMIN, TECNICO
 * - DELETE /api/projects/{id}/documents/{docId}: ADMIN, TECNICO
 */
@RestController
@RequestMapping("/api/projects/{projectId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Constructor del controlador de documentos.
     *
     * @param documentService Servicio de gestión de documentos
     */
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sube un nuevo documento de avance a un proyecto.
     *
     * @param projectId ID del proyecto
     * @param file Archivo a subir
     * @return Documento creado con sus metadatos
     * @throws EntityNotFoundException Si el proyecto no existe
     * @throws IOException Si ocurre un error al guardar el archivo
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjectDocumentResponse> upload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) throws IOException {
        ProjectDocumentResponse created = ProjectDocumentResponse.from(
                documentService.upload(projectId, file));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtiene los documentos de un proyecto.
     *
     * @param projectId ID del proyecto
     * @return Lista de documentos del proyecto
     * @throws EntityNotFoundException Si el proyecto no existe
     */
    @GetMapping
    public List<ProjectDocumentResponse> getDocuments(@PathVariable Long projectId) {
        return documentService.getDocumentsByProject(projectId);
    }

    /**
     * Descarga un documento por su ID.
     *
     * @param projectId ID del proyecto (para consistencia de ruta)
     * @param documentId ID del documento
     * @return Contenido del archivo con headers de descarga
     * @throws EntityNotFoundException Si el documento no existe
     * @throws IOException Si ocurre un error al leer el archivo
     */
    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long projectId,
            @PathVariable Long documentId) throws IOException {
        var document = documentService.getDocument(documentId);
        byte[] content = documentService.download(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .body(content);
    }

    /**
     * Borra un documento por su ID.
     *
     * @param projectId ID del proyecto (para consistencia de ruta)
     * @param documentId ID del documento
     * @return Respuesta vacía con estado 204
     * @throws EntityNotFoundException Si el documento no existe
     * @throws IOException Si ocurre un error al borrar el archivo
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long documentId) throws IOException {
        documentService.delete(documentId);
        return ResponseEntity.noContent().build();
    }
}

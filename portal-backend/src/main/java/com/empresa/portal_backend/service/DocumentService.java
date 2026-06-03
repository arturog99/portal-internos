package com.empresa.portal_backend.service;

import com.empresa.portal_backend.dto.ProjectDocumentResponse;
import com.empresa.portal_backend.model.Project;
import com.empresa.portal_backend.model.ProjectDocument;
import com.empresa.portal_backend.repository.ProjectDocumentRepository;
import com.empresa.portal_backend.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Servicio para la gestión de documentos de avance de proyectos.
 *
 * Proporciona operaciones de subida, listado, descarga y borrado de documentos,
 * aplicando las reglas de negocio y controlando el acceso por proyecto.
 */
@Service
public class DocumentService {

    private final ProjectDocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final DocumentStorageService storageService;

    /**
     * Constructor del servicio de documentos.
     *
     * @param documentRepository Repositorio de documentos
     * @param projectRepository Repositorio de proyectos
     * @param storageService Servicio de almacenamiento en disco
     */
    public DocumentService(
            ProjectDocumentRepository documentRepository,
            ProjectRepository projectRepository,
            DocumentStorageService storageService) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.storageService = storageService;
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
    @Transactional
    public ProjectDocument upload(Long projectId, MultipartFile file) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado: " + projectId));

        String storedFilename = storageService.store(file);
        String uploadedBy = getCurrentUsername();

        ProjectDocument document = ProjectDocument.builder()
                .filename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .contentType(file.getContentType())
                .size(file.getSize())
                .uploadedBy(uploadedBy)
                .uploadedAt(java.time.Instant.now())
                .project(project)
                .build();

        return documentRepository.save(document);
    }

    /**
     * Obtiene los documentos de un proyecto ordenados por fecha de subida descendente.
     *
     * @param projectId ID del proyecto
     * @return Lista de documentos del proyecto
     * @throws EntityNotFoundException Si el proyecto no existe
     */
    public List<ProjectDocumentResponse> getDocumentsByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Proyecto no encontrado: " + projectId);
        }
        return documentRepository.findByProjectIdOrderByUploadedAtDesc(projectId)
                .stream()
                .map(ProjectDocumentResponse::from)
                .toList();
    }

    /**
     * Obtiene un documento por su ID.
     *
     * @param documentId ID del documento
     * @return Documento encontrado
     * @throws EntityNotFoundException Si el documento no existe
     */
    public ProjectDocument getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado: " + documentId));
    }

    /**
     * Descarga el contenido de un documento.
     *
     * @param documentId ID del documento
     * @return Contenido del archivo como array de bytes
     * @throws EntityNotFoundException Si el documento no existe
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public byte[] download(Long documentId) throws IOException {
        ProjectDocument document = getDocument(documentId);
        return storageService.load(document.getStoredFilename());
    }

    /**
     * Borra un documento (metadatos y archivo en disco).
     *
     * @param documentId ID del documento
     * @throws EntityNotFoundException Si el documento no existe
     * @throws IOException Si ocurre un error al borrar el archivo
     */
    @Transactional
    public void delete(Long documentId) throws IOException {
        ProjectDocument document = getDocument(documentId);
        storageService.delete(document.getStoredFilename());
        documentRepository.delete(document);
    }

    /**
     * Obtiene el nombre de usuario autenticado actual.
     *
     * @return Nombre de usuario, o "unknown" si no hay autenticación
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() ? auth.getName() : "unknown";
    }
}

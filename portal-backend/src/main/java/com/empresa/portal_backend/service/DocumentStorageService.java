package com.empresa.portal_backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Servicio para el almacenamiento de documentos de avance en disco.
 *
 * Guarda los archivos en una carpeta configurable y genera nombres únicos
 * para evitar colisiones. Proporciona operaciones de subida, lectura y borrado.
 */
@Service
public class DocumentStorageService {

    /**
     * Directorio base donde se almacenan los documentos.
     * Configurable en application.properties (app.storage.documents-path).
     */
    @Value("${app.storage.documents-path:./documents}")
    private String documentsPath;

    /**
     * Directorio base como Path.
     */
    private Path baseLocation;

    /**
     * Inicializa el directorio de almacenamiento al arrancar el servicio.
     * Se ejecuta después de la inyección de dependencias (@Value).
     */
    @PostConstruct
    public void init() {
        this.baseLocation = Paths.get(documentsPath).toAbsolutePath().normalize();
    }

    /**
     * Asegura que el directorio de almacenamiento existe.
     *
     * @throws IOException Si no se puede crear el directorio
     */
    public void createDirectories() throws IOException {
        Files.createDirectories(this.baseLocation);
    }

    /**
     * Guarda un archivo subido en disco con un nombre único.
     *
     * @param file Archivo multipart subido
     * @return Nombre único generado para el archivo
     * @throws IOException Si ocurre un error al escribir el archivo
     */
    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No se puede guardar un archivo vacío");
        }

        // Asegura que el directorio existe antes de guardar
        Files.createDirectories(this.baseLocation);

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String storedFilename = UUID.randomUUID() + extension;

        Path targetLocation = this.baseLocation.resolve(storedFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return storedFilename;
    }

    /**
     * Lee un archivo almacenado en disco.
     *
     * @param storedFilename Nombre único del archivo
     * @return Contenido del archivo como array de bytes
     * @throws IOException Si el archivo no existe o no se puede leer
     */
    public byte[] load(String storedFilename) throws IOException {
        Path targetLocation = this.baseLocation.resolve(storedFilename);
        if (!Files.exists(targetLocation)) {
            throw new IOException("Archivo no encontrado: " + storedFilename);
        }
        return Files.readAllBytes(targetLocation);
    }

    /**
     * Borra un archivo del disco.
     *
     * @param storedFilename Nombre único del archivo
     * @throws IOException Si ocurre un error al borrar el archivo
     */
    public void delete(String storedFilename) throws IOException {
        Path targetLocation = this.baseLocation.resolve(storedFilename);
        Files.deleteIfExists(targetLocation);
    }
}

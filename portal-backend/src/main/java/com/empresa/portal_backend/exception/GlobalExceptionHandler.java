package com.empresa.portal_backend.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para convertir errores en respuestas JSON coherentes.
 * 
 * Este handler captura excepciones de la aplicación y las transforma en
 * respuestas HTTP con el código de estado apropiado y un formato JSON uniforme.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de campos en las solicitudes.
     *
     * @param ex Excepción de validación
     * @return Respuesta con los campos inválidos y sus mensajes de error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fields.put(err.getField(), err.getDefaultMessage()));
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Datos invalidos");
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Maneja errores de autenticación (credenciales inválidas o usuario no encontrado).
     *
     * @param ex Excepción de autenticación
     * @return Respuesta con estado 401 UNAUTHORIZED
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleAuth(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(baseBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    /**
     * Maneja errores de acceso denegado (falta de permisos).
     *
     * @param ex Excepción de acceso denegado
     * @return Respuesta con estado 403 FORBIDDEN
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(baseBody(HttpStatus.FORBIDDEN, "No tienes permisos para esta accion"));
    }

    /**
     * Maneja errores de entidad no encontrada.
     *
     * @param ex Excepción de entidad no encontrada
     * @return Respuesta con estado 404 NOT FOUND
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(baseBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Maneja errores de argumentos inválidos o estado ilegal.
     *
     * @param ex Excepción de argumento o estado
     * @return Respuesta con estado 400 BAD REQUEST
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(baseBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Maneja excepciones genéricas no capturadas específicamente.
     *
     * @param ex Excepción genérica
     * @return Respuesta con estado 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(baseBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    /**
     * Crea el cuerpo base de la respuesta de error.
     *
     * @param status Código HTTP de estado
     * @param message Mensaje de error
     * @return Mapa con timestamp, status, error y message
     */
    private Map<String, Object> baseBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}

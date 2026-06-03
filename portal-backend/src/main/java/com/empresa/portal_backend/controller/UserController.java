package com.empresa.portal_backend.controller;

import com.empresa.portal_backend.dto.UserRequest;
import com.empresa.portal_backend.dto.UserResponse;
import com.empresa.portal_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de usuarios del sistema.
 * 
 * <p>Proporciona operaciones CRUD completas para usuarios.
 * Solo accesible por usuarios con rol ADMIN (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructor del controlador de usuarios.
     *
     * @param userService Servicio de gestión de usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return Lista de usuarios
     */
    @GetMapping
    public List<UserResponse> getAll() {
        return userService.findAll().stream().map(UserResponse::from).toList();
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return UserResponse.from(userService.findById(id));
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param request Datos del usuario a crear
     * @return Usuario creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse created = UserResponse.from(userService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id ID del usuario a actualizar
     * @param request Nuevos datos del usuario
     * @return Usuario actualizado
     */
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return UserResponse.from(userService.update(id, request));
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param id ID del usuario a eliminar
     * @return Respuesta vacía con estado HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

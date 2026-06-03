package com.empresa.portal_backend.service;

import com.empresa.portal_backend.dto.UserRequest;
import com.empresa.portal_backend.model.User;
import com.empresa.portal_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de usuarios del sistema.
 * 
 * Proporciona operaciones CRUD para usuarios, incluyendo validación de
 * unicidad de username y email, y codificación segura de contraseñas.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor del servicio de usuarios.
     *
     * @param userRepository Repositorio de usuarios
     * @param passwordEncoder Codificador de contraseñas
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return Lista de todos los usuarios
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario
     * @return Usuario encontrado
     * @throws EntityNotFoundException Si no existe un usuario con ese ID
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * 
     * Valida que el username y email no estén en uso, codifica la contraseña
     * y crea el usuario con 2FA deshabilitado por defecto.
     *
     * @param request Datos del usuario a crear
     * @return Usuario creado
     * @throws IllegalArgumentException Si el username o email ya existen
     */
    @Transactional
    public User create(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .totpEnabled(false)
                .build();
        return userRepository.save(user);
    }

    /**
     * Actualiza los datos de un usuario existente.
     * 
     * La contraseña solo se actualiza si se proporciona un valor no vacío.
     *
     * @param id ID del usuario a actualizar
     * @param request Nuevos datos del usuario
     * @return Usuario actualizado
     * @throws EntityNotFoundException Si no existe un usuario con ese ID
     */
    @Transactional
    public User update(Long id, UserRequest request) {
        User user = findById(id);
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(request.role());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return userRepository.save(user);
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param id ID del usuario a eliminar
     * @throws EntityNotFoundException Si no existe un usuario con ese ID
     */
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado: " + id);
        }
        userRepository.deleteById(id);
    }
}

package com.empresa.portal_backend.repository;

import com.empresa.portal_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad User.
 * 
 * Proporciona métodos personalizados para buscar usuarios por username,
 * email o certificado digital, además de los métodos CRUD estándar.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar
     * @return Optional con el usuario encontrado, o vacío si no existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     *
     * @param email Email a buscar
     * @return Optional con el usuario encontrado, o vacío si no existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su identificador de certificado digital.
     *
     * @param certificateId Identificador del certificado (ej. DNI/NIF)
     * @return Optional con el usuario encontrado, o vacío si no existe
     */
    Optional<User> findByCertificateId(String certificateId);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     *
     * @param username Nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}

package com.empresa.portal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un usuario del portal interno.
 * 
 * Esta entidad almacena la información de autenticación y perfil de los usuarios,
 * incluyendo soporte para autenticación de dos factores (2FA/TOTP) y certificados digitales.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único para el login.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Email único del usuario.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Hash BCrypt de la contraseña (nunca se almacena en texto plano).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Rol del usuario en el sistema (ADMIN, TECNICO, VISITANTE).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Secreto TOTP para autenticación de dos factores (Base32).
     */
    private String totpSecret;

    /**
     * Indica si el usuario tiene 2FA habilitado.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean totpEnabled = false;

    /**
     * Identificador extraído del subject del certificado digital X.509 (ej. DNI/NIF).
     */
    private String certificateId;

    /**
     * Indica si la cuenta del usuario está activa.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
}

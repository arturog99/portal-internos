package com.empresa.portal_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entidad de usuario del portal interno
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // Hash BCrypt de la contrasena (nunca en texto plano)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ===== 2FA (TOTP / Google Authenticator) =====
    private String totpSecret;

    @Column(nullable = false)
    @Builder.Default
    private boolean totpEnabled = false;

    // ===== Certificado digital FNMT (X.509) =====
    // Identificador extraido del subject del certificado (p.ej. el DNI/NIF)
    private String certificateId;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
}

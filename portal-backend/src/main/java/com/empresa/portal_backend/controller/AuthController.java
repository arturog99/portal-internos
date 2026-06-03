package com.empresa.portal_backend.controller;

import com.empresa.portal_backend.dto.AuthResponse;
import com.empresa.portal_backend.dto.LoginRequest;
import com.empresa.portal_backend.dto.TwoFactorVerifyRequest;
import com.empresa.portal_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de autenticación con endpoints públicos.
 * 
 * Proporciona los endpoints para:
 * - Login de usuarios (con o sin 2FA)
 * - Verificación del código 2FA
 * 
 * Estos endpoints son públicos y no requieren autenticación previa.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor del controlador de autenticación.
     *
     * @param authService Servicio de autenticación
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint de login para autenticar usuarios.
     * 
     * Si el usuario tiene 2FA habilitado, devuelve un token temporal que requiere
     * verificación del código TOTP. Si no tiene 2FA, devuelve directamente el token
     * de acceso definitivo.
     *
     * @param request Credenciales de login (username y password)
     * @return Respuesta de autenticación con el token correspondiente
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint para verificar el código 2FA y obtener el token de acceso definitivo.
     * 
     * Este endpoint debe llamarse después de un login exitoso que devolvió un
     * token temporal (cuando el usuario tiene 2FA habilitado).
     *
     * @param request Token temporal del login y código TOTP de 6 dígitos
     * @return Respuesta de autenticación con el token de acceso definitivo
     */
    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponse> verifyTwoFactor(@Valid @RequestBody TwoFactorVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyTwoFactor(request));
    }
}

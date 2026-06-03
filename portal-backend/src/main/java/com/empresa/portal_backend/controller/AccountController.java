package com.empresa.portal_backend.controller;

import com.empresa.portal_backend.dto.MessageResponse;
import com.empresa.portal_backend.dto.TotpSetupResponse;
import com.empresa.portal_backend.dto.TwoFactorEnableRequest;
import com.empresa.portal_backend.dto.UserResponse;
import com.empresa.portal_backend.model.User;
import com.empresa.portal_backend.repository.UserRepository;
import com.empresa.portal_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para la gestión de la cuenta del usuario autenticado.
 * 
 * Proporciona endpoints para:
 * - Obtener el perfil del usuario autenticado
 * - Configurar la autenticación de dos factores (2FA)
 * - Activar el 2FA tras verificar el primer código
 * 
 * Todos los endpoints requieren autenticación previa.
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AuthService authService;
    private final UserRepository userRepository;

    /**
     * Constructor del controlador de cuenta.
     *
     * @param authService Servicio de autenticación
     * @param userRepository Repositorio de usuarios
     */
    public AccountController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene los datos del usuario autenticado.
     *
     * @param authentication Contexto de autenticación de Spring Security
     * @return Datos del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * Genera el secreto TOTP y el código QR para configurar el 2FA.
     * 
     * Este endpoint inicia el proceso de configuración de 2FA, generando
     * un nuevo secreto y el QR que el usuario debe escanear con su app de autenticación.
     *
     * @param authentication Contexto de autenticación de Spring Security
     * @return Respuesta con el secreto, URL otpauth y código QR en base64
     */
    @PostMapping("/2fa/setup")
    public ResponseEntity<TotpSetupResponse> setupTwoFactor(Authentication authentication) {
        return ResponseEntity.ok(authService.setupTotp(authentication.getName()));
    }

    /**
     * Activa el 2FA tras verificar el primer código introducido por el usuario.
     * 
     * Este endpoint debe llamarse después de que el usuario haya escaneado el QR
     * e introducido el primer código de su app de autenticación.
     *
     * @param authentication Contexto de autenticación de Spring Security
     * @param request Código TOTP de 6 dígitos
     * @return Mensaje de confirmación
     */
    @PostMapping("/2fa/enable")
    public ResponseEntity<MessageResponse> enableTwoFactor(Authentication authentication,
                                                           @Valid @RequestBody TwoFactorEnableRequest request) {
        authService.enableTotp(authentication.getName(), request.code());
        return ResponseEntity.ok(new MessageResponse("2FA activado correctamente"));
    }
}

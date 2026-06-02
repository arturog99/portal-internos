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

// Endpoints de la cuenta del usuario autenticado (perfil + configuracion 2FA).
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AccountController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // Datos del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    // Genera el secreto + QR para configurar el 2FA
    @PostMapping("/2fa/setup")
    public ResponseEntity<TotpSetupResponse> setupTwoFactor(Authentication authentication) {
        return ResponseEntity.ok(authService.setupTotp(authentication.getName()));
    }

    // Activa el 2FA tras verificar el primer codigo
    @PostMapping("/2fa/enable")
    public ResponseEntity<MessageResponse> enableTwoFactor(Authentication authentication,
                                                           @Valid @RequestBody TwoFactorEnableRequest request) {
        authService.enableTotp(authentication.getName(), request.code());
        return ResponseEntity.ok(new MessageResponse("2FA activado correctamente"));
    }
}

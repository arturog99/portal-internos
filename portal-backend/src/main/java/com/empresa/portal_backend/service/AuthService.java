package com.empresa.portal_backend.service;

import com.empresa.portal_backend.dto.AuthResponse;
import com.empresa.portal_backend.dto.LoginRequest;
import com.empresa.portal_backend.dto.TotpSetupResponse;
import com.empresa.portal_backend.dto.TwoFactorVerifyRequest;
import com.empresa.portal_backend.model.User;
import com.empresa.portal_backend.repository.UserRepository;
import com.empresa.portal_backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TotpService totpService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       TotpService totpService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.totpService = totpService;
    }

    // Paso 1: validar usuario/contrasena. Si el usuario tiene 2FA, se devuelve un token temporal.
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String role = user.getRole().name();

        if (user.isTotpEnabled()) {
            String tempToken = jwtService.generateTwoFactorToken(user.getUsername(), role);
            return AuthResponse.requires2fa(tempToken, user.getUsername(), role);
        }

        String token = jwtService.generateAccessToken(user.getUsername(), role);
        return AuthResponse.authenticated(token, user.getUsername(), role);
    }

    // Paso 2: verificar el codigo 2FA usando el token temporal del paso 1.
    public AuthResponse verifyTwoFactor(TwoFactorVerifyRequest request) {
        String tempToken = request.tempToken();
        if (!jwtService.isValid(tempToken) || !jwtService.isTwoFactorToken(tempToken)) {
            throw new BadCredentialsException("Token de 2FA invalido o expirado");
        }

        String username = jwtService.extractUsername(tempToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!totpService.verifyCode(user.getTotpSecret(), request.code())) {
            throw new BadCredentialsException("Codigo 2FA incorrecto");
        }

        String token = jwtService.generateAccessToken(user.getUsername(), user.getRole().name());
        return AuthResponse.authenticated(token, user.getUsername(), user.getRole().name());
    }

    // Genera un nuevo secreto TOTP para el usuario y devuelve el QR para escanear.
    @Transactional
    public TotpSetupResponse setupTotp(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String secret = totpService.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(false); // se activa tras verificar el primer codigo
        userRepository.save(user);

        String otpAuthUrl = totpService.buildOtpAuthUrl(user.getEmail(), secret);
        String qr = totpService.generateQrCodeDataUri(user.getEmail(), secret);
        return new TotpSetupResponse(secret, otpAuthUrl, qr);
    }

    // Activa el 2FA tras verificar el primer codigo introducido por el usuario.
    @Transactional
    public void enableTotp(String username, String code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (user.getTotpSecret() == null) {
            throw new IllegalStateException("Primero debes generar el secreto 2FA");
        }
        if (!totpService.verifyCode(user.getTotpSecret(), code)) {
            throw new BadCredentialsException("Codigo 2FA incorrecto");
        }
        user.setTotpEnabled(true);
        userRepository.save(user);
    }
}

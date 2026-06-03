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

/**
 * Servicio de autenticación que gestiona el login, verificación 2FA y configuración TOTP.
 * 
 * Este servicio implementa un flujo de autenticación en dos pasos cuando el usuario tiene
 * 2FA habilitado:
 * 1. Paso 1: Validar usuario/contraseña → devuelve token temporal si tiene 2FA
 * 2. Paso 2: Verificar código 2FA → devuelve token de acceso definitivo
 * 
 * Si el usuario no tiene 2FA habilitado, el paso 1 devuelve directamente el token de acceso.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TotpService totpService;

    /**
     * Constructor del servicio de autenticación.
     *
     * @param authenticationManager Gestor de autenticación de Spring Security
     * @param userRepository Repositorio de usuarios
     * @param jwtService Servicio para generar y validar tokens JWT
     * @param totpService Servicio para gestión de TOTP (2FA)
     */
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       TotpService totpService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.totpService = totpService;
    }

    /**
     * Paso 1 del flujo de autenticación: valida usuario y contraseña.
     * 
     * Si el usuario tiene 2FA habilitado, devuelve un token temporal que requiere
     * verificación del código TOTP. Si no tiene 2FA, devuelve directamente el token
     * de acceso definitivo.
     *
     * @param request Credenciales de login (username y password)
     * @return Respuesta de autenticación con el token correspondiente
     * @throws BadCredentialsException Si las credenciales son inválidas
     * @throws UsernameNotFoundException Si el usuario no existe
     */
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

    /**
     * Paso 2 del flujo de autenticación: verifica el código 2FA.
     * 
     * Valida el token temporal del paso 1 y el código TOTP proporcionado por el usuario.
     * Si ambos son correctos, genera y devuelve el token de acceso definitivo.
     *
     * @param request Token temporal del paso 1 y código TOTP de 6 dígitos
     * @return Respuesta de autenticación con el token de acceso definitivo
     * @throws BadCredentialsException Si el token es inválido o el código 2FA es incorrecto
     * @throws UsernameNotFoundException Si el usuario no existe
     */
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

    /**
     * Login mediante certificado digital (X.509 / mTLS).
     * 
     * Se invoca cuando el usuario ya ha sido autenticado por su certificado en el
     * handshake TLS (el filtro X.509 ha resuelto el principal contra el campo
     * certificateId). Genera y devuelve el token de acceso JWT, igual que un login
     * normal, para que el frontend pueda seguir operando con JWT.
     *
     * @param username Nombre de usuario resuelto a partir del certificado
     * @return Respuesta de autenticación con el token de acceso
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    public AuthResponse certLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        String token = jwtService.generateAccessToken(user.getUsername(), user.getRole().name());
        return AuthResponse.authenticated(token, user.getUsername(), user.getRole().name());
    }

    /**
     * Genera un nuevo secreto TOTP para el usuario y prepara la configuración 2FA.
     * 
     * Este método:
     * - Genera un nuevo secreto TOTP aleatorio
     * - Lo guarda en el usuario (sin activar 2FA aún)
     * - Genera la URL otpauth:// para apps de autenticación
     * - Genera el código QR para escanear
     * 
     * El 2FA se activa realmente después de verificar el primer código con {@link #enableTotp}.
     *
     * @param username Nombre de usuario que quiere configurar 2FA
     * @return Respuesta con el secreto, URL otpauth y código QR en base64
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Transactional
    public TotpSetupResponse setupTotp(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String secret = totpService.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(false); // se activa tras verificar el primer código
        userRepository.save(user);

        String otpAuthUrl = totpService.buildOtpAuthUrl(user.getEmail(), secret);
        String qr = totpService.generateQrCodeDataUri(user.getEmail(), secret);
        return new TotpSetupResponse(secret, otpAuthUrl, qr);
    }

    /**
     * Activa el 2FA tras verificar el primer código introducido por el usuario.
     * 
     * Este método debe llamarse después de {@link #setupTotp} y de que el usuario
     * haya escaneado el QR e introducido el primer código de su app de autenticación.
     *
     * @param username Nombre de usuario
     * @param code Código TOTP de 6 dígitos generado por la app del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     * @throws IllegalStateException Si no se ha generado el secreto 2FA previamente
     * @throws BadCredentialsException Si el código 2FA es incorrecto
     */
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

package com.empresa.portal_backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Servicio para la autenticación de dos factores (2FA) basada en TOTP.
 * 
 * Este servicio implementa el estándar TOTP (Time-based One-Time Password)
 * compatible con aplicaciones de autenticación como:
 * - Google Authenticator
 * - Authy
 * - Microsoft Authenticator
 * - Otras apps compatibles con TOTP
 * 
 * Funcionalidades:
 * - Generación de secretos TOTP (Base32)
 * - Generación de códigos QR para escanear
 * - Verificación de códigos de 6 dígitos
 */
@Service
public class TotpService {

    private static final String ISSUER = "Portal Internos";
    private static final int QR_CODE_SIZE = 250;

    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    /**
     * Genera un nuevo secreto TOTP en formato Base32.
     * 
     * Este secreto se usa para configurar la app de autenticación del usuario
     * y debe guardarse de forma segura en la base de datos.
     *
     * @return Secreto TOTP en formato Base32
     */
    public String generateSecret() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    /**
     * Construye la URL otpauth:// para configurar la app de autenticación.
     * 
     * Esta URL sigue el formato estándar otpauth://totp/ISSUER:ACCOUNT?secret=SECRET
     * y puede ser escaneada directamente por apps de autenticación.
     *
     * @param account Identificador de la cuenta (usualmente el email)
     * @param secret Secreto TOTP en formato Base32
     * @return URL otpauth:// completa
     */
    public String buildOtpAuthUrl(String account, String secret) {
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secret).build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(ISSUER, account, key);
    }

    /**
     * Genera la imagen del código QR en formato data URI (base64).
     * 
     * El QR contiene la URL otpauth:// y puede mostrarse directamente en el frontend
     * usando la etiqueta img con src="data:image/png;base64,..."
     *
     * @param account Identificador de la cuenta (usualmente el email)
     * @param secret Secreto TOTP en formato Base32
     * @return Data URI del código QR en formato PNG
     * @throws IllegalStateException Si hay error al generar el QR
     */
    public String generateQrCodeDataUri(String account, String secret) {
        String otpAuthUrl = buildOtpAuthUrl(account, secret);
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(otpAuthUrl, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("No se pudo generar el codigo QR", e);
        }
    }

    /**
     * Verifica si un código TOTP de 6 dígitos es válido para el secreto dado.
     * 
     * Este método valida el código contra el secreto usando el algoritmo TOTP,
     * que permite una ventana de tiempo para compensar desincronizaciones leves.
     *
     * @param secret Secreto TOTP en formato Base32
     * @param code Código de 6 dígitos introducido por el usuario
     * @return true si el código es válido, false en caso contrario
     */
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null) {
            return false;
        }
        try {
            return googleAuthenticator.authorize(secret, Integer.parseInt(code.trim()));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

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

// Servicio de 2FA basado en TOTP (compatible con Google Authenticator, Authy, etc.).
@Service
public class TotpService {

    private static final String ISSUER = "Portal Internos";

    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    // Genera un nuevo secreto TOTP (base32)
    public String generateSecret() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    // Construye la URL otpauth:// que codifica el QR
    public String buildOtpAuthUrl(String account, String secret) {
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secret).build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(ISSUER, account, key);
    }

    // Genera la imagen del QR (PNG en base64 data URI) a partir del secreto
    public String generateQrCodeDataUri(String account, String secret) {
        String otpAuthUrl = buildOtpAuthUrl(account, secret);
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("No se pudo generar el codigo QR", e);
        }
    }

    // Verifica el codigo de 6 digitos contra el secreto
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

package com.empresa.portal_backend.dto;

/**
 * Datos para configurar el 2FA en la app de autenticación.
 * 
 * - secret: clave en base32 (por si se introduce manualmente)
 * - otpAuthUrl: URL otpauth:// para generar el QR
 * - qrCodeDataUri: imagen PNG del QR en base64 (data URI), lista para <img src="...">
 */
public record TotpSetupResponse(
        /** Secreto TOTP en formato Base32 */
        String secret,
        /** URL otpauth:// para configurar la app de autenticación */
        String otpAuthUrl,
        /** Imagen PNG del código QR en base64 (data URI) */
        String qrCodeDataUri
) {
}

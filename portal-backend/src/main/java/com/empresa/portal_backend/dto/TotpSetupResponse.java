package com.empresa.portal_backend.dto;

// Datos para configurar el 2FA en la app de autenticacion:
// - secret: clave en base32 (por si se introduce manualmente)
// - otpAuthUrl: URL otpauth:// para generar el QR
// - qrCodeDataUri: imagen PNG del QR en base64 (data URI), lista para <img src="...">
public record TotpSetupResponse(
        String secret,
        String otpAuthUrl,
        String qrCodeDataUri
) {
}

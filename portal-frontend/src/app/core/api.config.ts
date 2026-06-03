/**
 * Configuración central de la API del backend.
 *
 * Define la URL base del backend Spring Boot. Centralizarla aquí permite
 * cambiar el host/puerto en un único sitio.
 */
export const API_BASE_URL = 'https://localhost:8443/api';

/**
 * URL base para el login por certificado digital (X.509 / mTLS).
 *
 * El backend sirve todo (usuario/clave, 2FA y certificado) por el mismo puerto
 * HTTPS con TLS mutuo, por lo que coincide con API_BASE_URL. El navegador
 * presenta el certificado durante el handshake TLS al llamar a /auth/cert-login.
 */
export const CERT_API_BASE_URL = API_BASE_URL;

/**
 * Configuración central de la API del backend.
 *
 * Define la URL base del backend Spring Boot. Centralizarla aquí permite
 * cambiar el host/puerto en un único sitio.
 */
export const API_BASE_URL = 'http://localhost:8080/api';

/**
 * URL base del backend en modo certificado digital (HTTPS con TLS mutuo).
 *
 * El login por certificado X.509 requiere mTLS, que solo está disponible en el
 * perfil "cert" del backend (puerto 8443). El navegador presentará el certificado
 * durante el handshake TLS al llamar a este endpoint.
 */
export const CERT_API_BASE_URL = 'https://localhost:8443/api';

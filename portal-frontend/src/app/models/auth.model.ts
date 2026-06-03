/**
 * Modelos relacionados con la autenticación y los usuarios.
 *
 * Reflejan los DTOs que expone el backend (AuthResponse, UserResponse, etc.).
 */

/** Roles disponibles en el sistema (coinciden con el enum del backend). */
export type Role = 'ADMIN' | 'TECNICO' | 'VISITANTE';

/** Respuesta del backend en el login y la verificación 2FA. */
export interface AuthResponse {
  /** Indica si el usuario debe verificar un código 2FA antes de obtener el token. */
  twoFactorRequired: boolean;
  /** Token JWT de acceso (null si se requiere 2FA). */
  token: string | null;
  /** Token temporal para el paso de verificación 2FA (null si no aplica). */
  tempToken: string | null;
  /** Nombre de usuario autenticado. */
  username: string;
  /** Rol del usuario autenticado. */
  role: Role;
}

/** Datos del usuario autenticado que persistimos en el cliente. */
export interface AuthUser {
  username: string;
  role: Role;
}

/** Respuesta del backend con los datos de un usuario. */
export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
  totpEnabled: boolean;
  certificateId: string | null;
  enabled: boolean;
}

/** Datos para crear o actualizar un usuario. */
export interface UserRequest {
  username: string;
  email: string;
  password: string;
  role: Role;
}

/** Respuesta del backend al iniciar la configuración de 2FA. */
export interface TotpSetup {
  /** Secreto en Base32 (para introducir manualmente). */
  secret: string;
  /** URL otpauth:// para apps de autenticación. */
  otpAuthUrl: string;
  /** Imagen PNG del QR en base64 (data URI) lista para usar en <img src>. */
  qrCodeDataUri: string;
}

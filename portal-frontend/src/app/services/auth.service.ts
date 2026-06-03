/**
 * Servicio de autenticación del frontend.
 *
 * Gestiona el flujo de login en dos pasos (usuario/clave + 2FA TOTP),
 * el almacenamiento del token JWT, el estado del usuario autenticado
 * y los helpers de permisos por rol.
 */
import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL, CERT_API_BASE_URL } from '../core/api.config';
import {
  AuthResponse,
  AuthUser,
  Role,
  TotpSetup,
} from '../models/auth.model';

/** Clave bajo la que se guarda el token JWT en localStorage. */
const TOKEN_KEY = 'portal_token';
/** Clave bajo la que se guardan los datos del usuario en localStorage. */
const USER_KEY = 'portal_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = `${API_BASE_URL}/auth`;
  private certBase = `${CERT_API_BASE_URL}/auth`;

  /** Usuario autenticado actual (null si no hay sesión). */
  currentUser = signal<AuthUser | null>(this.loadUser());

  /** Indica si hay una sesión activa. */
  isAuthenticated = computed(() => this.currentUser() !== null);

  /** Rol del usuario autenticado (o null). */
  role = computed<Role | null>(() => this.currentUser()?.role ?? null);

  /** True si el usuario es ADMIN. */
  isAdmin = computed(() => this.role() === 'ADMIN');

  /** True si el usuario puede editar proyectos (ADMIN o TECNICO). */
  canEdit = computed(() => this.role() === 'ADMIN' || this.role() === 'TECNICO');

  /** True si el usuario puede crear/borrar proyectos y gestionar usuarios (ADMIN). */
  canManage = computed(() => this.role() === 'ADMIN');

  /**
   * Paso 1 del login: valida usuario y contraseña.
   * Si el backend no requiere 2FA, guarda directamente la sesión.
   */
  login(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/login`, { username, password })
      .pipe(
        tap((res) => {
          if (!res.twoFactorRequired && res.token) {
            this.storeSession(res.token, { username: res.username, role: res.role });
          }
        })
      );
  }

  /**
   * Paso 2 del login: verifica el código 2FA con el token temporal
   * y guarda la sesión definitiva.
   */
  verifyTwoFactor(tempToken: string, code: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/verify-2fa`, { tempToken, code })
      .pipe(
        tap((res) => {
          if (res.token) {
            this.storeSession(res.token, { username: res.username, role: res.role });
          }
        })
      );
  }

  /**
   * Login mediante certificado digital (X.509 / mTLS).
   *
   * Llama al endpoint HTTPS protegido por TLS mutuo; el navegador presentará el
   * certificado durante el handshake. Si es válido, el backend devuelve el JWT y
   * se guarda la sesión como en el login normal.
   */
  certLogin(): Observable<AuthResponse> {
    return this.http
      .get<AuthResponse>(`${this.certBase}/cert-login`, { withCredentials: true })
      .pipe(
        tap((res) => {
          if (res.token) {
            this.storeSession(res.token, { username: res.username, role: res.role });
          }
        })
      );
  }

  /** Cierra la sesión y limpia el almacenamiento local. */
  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
  }

  /** Devuelve el token JWT almacenado (o null). */
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  /** Guarda el token y los datos del usuario en localStorage y en el estado. */
  private storeSession(token: string, user: AuthUser): void {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  /** Recupera el usuario almacenado al iniciar la app. */
  private loadUser(): AuthUser | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      return null;
    }
  }
}

/**
 * Servicio para la cuenta del usuario autenticado.
 *
 * Permite consultar el perfil y configurar/activar la autenticación
 * de dos factores (2FA TOTP).
 */
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../core/api.config';
import { TotpSetup, UserResponse } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private http = inject(HttpClient);
  private base = `${API_BASE_URL}/account`;

  /** Obtiene el perfil del usuario autenticado. */
  me(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/me`);
  }

  /** Genera el secreto y el QR para configurar el 2FA. */
  setupTotp(): Observable<TotpSetup> {
    return this.http.post<TotpSetup>(`${this.base}/2fa/setup`, {});
  }

  /** Activa el 2FA tras verificar el primer código. */
  enableTotp(code: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.base}/2fa/enable`, { code });
  }
}

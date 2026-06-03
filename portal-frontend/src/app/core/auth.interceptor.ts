/**
 * Interceptor HTTP funcional para la autenticación.
 *
 * - Añade la cabecera Authorization: Bearer <token> a las peticiones al backend.
 * - Si el backend responde 401 (token inválido/expirado), cierra la sesión
 *   y redirige al login.
 */
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { API_BASE_URL } from './api.config';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();

  // Solo añadimos el token a las peticiones dirigidas al backend.
  const isApiRequest = req.url.startsWith(API_BASE_URL);
  const authReq =
    token && isApiRequest
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(authReq).pipe(
    catchError((error) => {
      // No deslogueamos en el propio login para poder mostrar "credenciales inválidas".
      const isAuthEndpoint = req.url.includes('/auth/');
      if (error.status === 401 && !isAuthEndpoint) {
        auth.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};

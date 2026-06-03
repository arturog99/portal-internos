/**
 * Guards funcionales de rutas.
 *
 * - authGuard: exige sesión activa; redirige a /login si no la hay.
 * - adminGuard: exige rol ADMIN; redirige a /proyectos si no lo es.
 */
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }
  return router.createUrlTree(['/login']);
};

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAdmin()) {
    return true;
  }
  return router.createUrlTree(['/proyectos']);
};

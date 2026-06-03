/**
 * Configuración global de la aplicación Angular.
 * 
 * Configura los providers necesarios para el funcionamiento de la app:
 * - Manejo global de errores en el navegador
 * - Sistema de enrutamiento con las rutas definidas
 * - Cliente HTTP para peticiones al backend
 */
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient()
  ]
};

// Importaciones de configuración de Angular
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';

// Configuración global de la aplicación
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),  // Manejo global de errores en el navegador
    provideRouter(routes),                 // Configuración del router con las rutas definidas
    provideHttpClient()                    // Habilitar HttpClient para peticiones HTTP
  ]
};

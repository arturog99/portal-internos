/**
 * Configuración de rutas de la aplicación.
 * 
 * Define las rutas disponibles en la aplicación:
 * - / → Redirige a /proyectos
 * - /proyectos → Lista de proyectos
 * - /tecnologias → Página de tecnologías
 * - /documentacion → Página de documentación
 * - ** → Cualquier otra ruta redirige a /proyectos
 */
import { Routes } from '@angular/router';
import { ProjectListComponent } from './components/project-list/project-list';
import { Technologies } from './components/technologies/technologies';
import { Documentation } from './components/documentation/documentation';

export const routes: Routes = [
  { path: '', redirectTo: '/proyectos', pathMatch: 'full' },
  { path: 'proyectos', component: ProjectListComponent },
  { path: 'tecnologias', component: Technologies },
  { path: 'documentacion', component: Documentation },
  { path: '**', redirectTo: '/proyectos' }
];

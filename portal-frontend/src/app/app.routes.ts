// Importaciones de Angular Router y componentes
import { Routes } from '@angular/router';
import { ProjectListComponent } from './components/project-list/project-list';
import { Technologies } from './components/technologies/technologies';
import { Documentation } from './components/documentation/documentation';

// Configuración de rutas de la aplicación
export const routes: Routes = [
  { path: '', redirectTo: '/proyectos', pathMatch: 'full' },  // Redirigir raíz a proyectos
  { path: 'proyectos', component: ProjectListComponent },     // Ruta: /proyectos -> Lista de proyectos
  { path: 'tecnologias', component: Technologies },            // Ruta: /tecnologias -> Página de tecnologías
  { path: 'documentacion', component: Documentation },         // Ruta: /documentacion -> Página de documentación
  { path: '**', redirectTo: '/proyectos' }                      // Ruta wildcard -> Redirigir a proyectos
];

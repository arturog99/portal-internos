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

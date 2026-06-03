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
import { LoginComponent } from './components/login/login';
import { AccountComponent } from './components/account/account';
import { UsersComponent } from './components/users/users';
import { authGuard, adminGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/proyectos', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'proyectos', component: ProjectListComponent, canActivate: [authGuard] },
  { path: 'tecnologias', component: Technologies, canActivate: [authGuard] },
  { path: 'documentacion', component: Documentation, canActivate: [authGuard] },
  { path: 'cuenta', component: AccountComponent, canActivate: [authGuard] },
  { path: 'usuarios', component: UsersComponent, canActivate: [adminGuard] },
  { path: '**', redirectTo: '/proyectos' }
];

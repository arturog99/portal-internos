/**
 * Componente de barra de navegación principal.
 * 
 * Muestra los enlaces de navegación a las diferentes secciones de la aplicación:
 * - Proyectos
 * - Tecnologías
 * - Documentación
 * 
 * Utiliza RouterLink para la navegación y RouterLinkActive para resaltar
 * la ruta activa visualmente.
 */
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  /** Servicio de autenticación, expuesto a la plantilla para mostrar usuario/rol. */
  protected auth = inject(AuthService);
  private router = inject(Router);

  /** Cierra la sesión y vuelve al login. */
  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}

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
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  // Componente sin lógica adicional, solo muestra la navegación
}

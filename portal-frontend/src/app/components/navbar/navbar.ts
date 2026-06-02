// Importaciones de Angular y Router
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

// Componente de barra de navegación
@Component({
  selector: 'app-navbar',                     // Selector HTML para usar el componente
  standalone: true,                            // Componente standalone (independiente)
  imports: [RouterLink, RouterLinkActive],     // Directivas para navegación y estado activo
  templateUrl: './navbar.html',                // Archivo de plantilla HTML
  styleUrl: './navbar.css',                    // Archivo de estilos CSS
})
export class Navbar {
  // Componente sin lógica adicional, solo muestra la navegación
}

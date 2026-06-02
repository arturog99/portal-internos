// Importaciones de Angular
import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';

// Componente principal de la aplicación
@Component({
  selector: 'app-root',           // Selector HTML para usar el componente
  imports: [RouterOutlet, Navbar], // Módulos importados: RouterOutlet para navegación, Navbar para la barra
  templateUrl: './app.html',        // Archivo de plantilla HTML
  styleUrl: './app.css'            // Archivo de estilos CSS
})
export class App {
  // Signal que contiene el título de la aplicación (reactivo)
  protected readonly title = signal('portal-internos');
}

/**
 * Componente principal (root) de la aplicación.
 * 
 * Este es el componente raíz que contiene:
 * - La barra de navegación (Navbar)
 * - El RouterOutlet donde se renderizan las rutas hijas
 * 
 * Es el punto de entrada de la aplicación Angular.
 */
import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  /** Título de la aplicación (usado en el header y meta tags) */
  protected readonly title = signal('portal-internos');
}

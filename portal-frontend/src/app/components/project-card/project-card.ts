/**
 * Componente de tarjeta de proyecto individual.
 *
 * Muestra la información de un proyecto en formato de tarjeta:
 * - Nombre
 * - Descripción
 * - Tags de tecnologías
 * - Estado con código de colores
 *
 * Este componente es usado dentro de ProjectListComponent.
 */
import { Component } from '@angular/core';

@Component({
  selector: 'app-project-card',
  imports: [],
  templateUrl: './project-card.html',
  styleUrl: './project-card.css',
})
export class ProjectCard {}

/**
 * Componente de página de tecnologías.
 * 
 * Muestra un listado de todas las tecnologías utilizadas en los proyectos,
 * ordenadas por frecuencia de uso (de más a menos usadas).
 * 
 * Calcula dinámicamente las tecnologías a partir de los tags de los proyectos
 * y muestra cuántos proyectos usan cada tecnología.
 */
import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-technologies',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './technologies.html',
  styleUrl: './technologies.css',
})
export class Technologies implements OnInit {
  /** Servicio para obtener los datos de proyectos */
  private projectService = inject(ProjectService);

  /** Signal con la lista de proyectos cargados */
  projects = signal<Project[]>([]);

  /**
   * Computed que calcula las tecnologías únicas con su conteo de uso.
   * 
   * @returns Array de objetos {name, count} ordenado por conteo descendente
   */
  technologies = computed(() => {
    const projects = this.projects();
    const techMap = new Map<string, number>();

    // Contar cuántos proyectos usan cada tecnología
    projects.forEach(project => {
      project.tags.forEach(tag => {
        techMap.set(tag, (techMap.get(tag) || 0) + 1);
      });
    });

    // Convertir el mapa a array y ordenar por conteo (más usadas primero)
    return Array.from(techMap.entries())
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count);
  });

  /** Carga los proyectos al inicializar el componente */
  ngOnInit() {
    this.projectService.getProjects().subscribe({
      next: (datos) => {
        this.projects.set(datos);
      },
      error: (err) => {
        console.error('Error cargando proyectos:', err);
      }
    });
  }
}

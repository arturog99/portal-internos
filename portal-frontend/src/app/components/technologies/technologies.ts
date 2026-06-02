// Importaciones de Angular, servicios y modelos
import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';

// Componente de página de tecnologías
@Component({
  selector: 'app-technologies',          // Selector HTML para usar el componente
  standalone: true,                      // Componente standalone (independiente)
  imports: [CommonModule],              // CommonModule para directivas como @for
  templateUrl: './technologies.html',   // Archivo de plantilla HTML
  styleUrl: './technologies.css',       // Archivo de estilos CSS
})
export class Technologies implements OnInit {
  // Inyección del servicio de proyectos
  private projectService = inject(ProjectService);

  // Signal para almacenar la lista de proyectos
  projects = signal<Project[]>([]);

  // Computed: obtiene todas las tecnologías únicas con conteo de proyectos que las usan
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

  // Al inicializar el componente, cargar los proyectos desde el servicio
  ngOnInit() {
    this.projectService.getProjects().subscribe({
      next: (datos) => {
        this.projects.set(datos);  // Actualizar el signal con los datos recibidos
      },
      error: (err) => {
        console.error('Error cargando proyectos:', err);
      }
    });
  }
}

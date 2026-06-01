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
  private projectService = inject(ProjectService);

  projects = signal<Project[]>([]);

  // Obtener todas las tecnologías únicas con conteo de proyectos
  technologies = computed(() => {
    const projects = this.projects();
    const techMap = new Map<string, number>();

    projects.forEach(project => {
      project.tags.forEach(tag => {
        techMap.set(tag, (techMap.get(tag) || 0) + 1);
      });
    });

    return Array.from(techMap.entries())
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count);
  });

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

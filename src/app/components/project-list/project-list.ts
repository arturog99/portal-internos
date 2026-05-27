import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './project-list.html',
  styleUrl: './project-list.css',
})
export class ProjectListComponent implements OnInit {
  private projectService = inject(ProjectService);
  
  projects = signal<Project[]>([]);
  searchTerm = signal('');

  filteredProjects = computed(() => {
    const projects = this.projects();
    const term = this.searchTerm();
    
    if (!term) {
      return projects;
    }
    return projects.filter(proyecto =>
      proyecto.name.toLowerCase().includes(term.toLowerCase())
    );
  });

  ngOnInit() {
    this.projectService.getProjects().subscribe({
      next: (datos) => {
        console.log('Datos recibidos:', datos);
        this.projects.set(datos);
      },
      error: (err) => {
        console.error('Error cargando proyectos:', err);
      }
    });
  }
}

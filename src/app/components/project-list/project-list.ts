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
  selectedStatus = signal<string>('');
  selectedTag = signal<string>('');

  // Obtener todos los tags únicos de los proyectos
  availableTags = computed(() => {
    const projects = this.projects();
    const allTags = projects.flatMap(p => p.tags);
    return Array.from(new Set(allTags)).sort();
  });

  filteredProjects = computed(() => {
    const projects = this.projects();
    const term = this.searchTerm();
    const status = this.selectedStatus();
    const tag = this.selectedTag();
    
    return projects.filter(proyecto => {
      // Filtro por nombre
      const matchesName = !term || proyecto.name.toLowerCase().includes(term.toLowerCase());
      
      // Filtro por estado
      const matchesStatus = !status || proyecto.status === status;
      
      // Filtro por tecnología
      const matchesTag = !tag || proyecto.tags.includes(tag);
      
      return matchesName && matchesStatus && matchesTag;
    });
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

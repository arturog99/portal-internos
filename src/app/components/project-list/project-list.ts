import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../services/project';
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
  
  projects: Project[] = [];
  searchTerm: string = '';

  ngOnInit() {
    this.projectService.getProjects().subscribe(datos => {
      this.projects = datos; 
    });
  }

  get filteredProjects(): Project[] {
    if (!this.searchTerm) {
      return this.projects;
    }
    return this.projects.filter(proyecto =>
      proyecto.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
}

// Importaciones de Angular, servicios y modelos
import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';

// Componente de lista de proyectos con filtrado
@Component({
  selector: 'app-project-list',              // Selector HTML para usar el componente
  standalone: true,                          // Componente standalone (independiente)
  imports: [CommonModule, FormsModule],      // CommonModule para directivas, FormsModule para formularios
  templateUrl: './project-list.html',         // Archivo de plantilla HTML
  styleUrl: './project-list.css',            // Archivo de estilos CSS
})
export class ProjectListComponent implements OnInit {
  // Inyección del servicio de proyectos
  private projectService = inject(ProjectService);
  
  // Signals para el estado reactivo de la aplicación
  projects = signal<Project[]>([]);          // Lista de proyectos
  searchTerm = signal('');                   // Texto de búsqueda
  selectedStatus = signal<string>('');       // Estado seleccionado para filtrar
  selectedFrontendTag = signal<string>(''); // Tecnología frontend seleccionada
  selectedBackendTag = signal<string>('');   // Tecnología backend seleccionada
  selectedDatabaseTag = signal<string>('');  // Tecnología base de datos seleccionada
  selectedCloudTag = signal<string>('');     // Tecnología cloud/infraestructura seleccionada

  // Categorías de tecnologías
  frontendTechs = ['Angular', 'React', 'Vue', 'React Native', 'HTML/CSS', 'JavaScript', 'TypeScript', 'Tailwind'];
  backendTechs = ['Node.js', 'PHP', 'Python', 'Java', 'Express'];
  databaseTechs = ['MongoDB', 'MySQL', 'SQL', 'PostgreSQL'];
  cloudTechs = ['AWS', 'Firebase', 'WebSockets'];

  // Computed: obtiene los tags únicos por categoría
  availableFrontendTags = computed(() => {
    const projects = this.projects();
    const allTags = projects.flatMap(p => p.tags);
    return this.frontendTechs.filter(tag => allTags.includes(tag)).sort();
  });

  availableBackendTags = computed(() => {
    const projects = this.projects();
    const allTags = projects.flatMap(p => p.tags);
    return this.backendTechs.filter(tag => allTags.includes(tag)).sort();
  });

  availableDatabaseTags = computed(() => {
    const projects = this.projects();
    const allTags = projects.flatMap(p => p.tags);
    return this.databaseTechs.filter(tag => allTags.includes(tag)).sort();
  });

  availableCloudTags = computed(() => {
    const projects = this.projects();
    const allTags = projects.flatMap(p => p.tags);
    return this.cloudTechs.filter(tag => allTags.includes(tag)).sort();
  });

  // Computed: filtra proyectos según búsqueda, estado y tecnologías
  filteredProjects = computed(() => {
    const projects = this.projects();
    const term = this.searchTerm();
    const status = this.selectedStatus();
    const frontendTag = this.selectedFrontendTag();
    const backendTag = this.selectedBackendTag();
    const databaseTag = this.selectedDatabaseTag();
    const cloudTag = this.selectedCloudTag();
    
    return projects.filter(proyecto => {
      // Filtro por nombre (búsqueda)
      const matchesName = !term || proyecto.name.toLowerCase().includes(term.toLowerCase());
      
      // Filtro por estado
      const matchesStatus = !status || proyecto.status === status;
      
      // Filtro por tecnologías (cualquiera de las categorías seleccionadas)
      const matchesFrontend = !frontendTag || proyecto.tags.includes(frontendTag);
      const matchesBackend = !backendTag || proyecto.tags.includes(backendTag);
      const matchesDatabase = !databaseTag || proyecto.tags.includes(databaseTag);
      const matchesCloud = !cloudTag || proyecto.tags.includes(cloudTag);
      
      const matchesAnyTech = matchesFrontend && matchesBackend && matchesDatabase && matchesCloud;
      
      return matchesName && matchesStatus && matchesAnyTech;
    });
  });

  // Al inicializar el componente, cargar los proyectos desde el servicio
  ngOnInit() {
    this.projectService.getProjects().subscribe({
      next: (datos) => {
        console.log('Datos recibidos:', datos);
        this.projects.set(datos);  // Actualizar el signal con los datos recibidos
      },
      error: (err) => {
        console.error('Error cargando proyectos:', err);
      }
    });
  }
}

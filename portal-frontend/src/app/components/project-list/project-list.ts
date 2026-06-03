// Importaciones de Angular, servicios y modelos
import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { ProjectRequest } from '../../models/project.request';
import { AuthService } from '../../services/auth.service';

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
  // Servicio de autenticación expuesto a la plantilla para permisos por rol
  protected auth = inject(AuthService);
  
  // Signals para el estado reactivo de la aplicación
  projects = signal<Project[]>([]);          // Lista de proyectos
  searchTerm = signal('');                   // Texto de búsqueda
  selectedStatus = signal<string>('');       // Estado seleccionado para filtrar
  selectedFrontendTag = signal<string>(''); // Tecnología frontend seleccionada
  selectedBackendTag = signal<string>('');   // Tecnología backend seleccionada
  selectedDatabaseTag = signal<string>('');  // Tecnología base de datos seleccionada
  selectedCloudTag = signal<string>('');     // Tecnología cloud/infraestructura seleccionada

  filtersExpanded = signal(false);           // Controla si el panel de filtros está desplegado
  selectedProject = signal<Project | null>(null);  // Proyecto seleccionado para ver detalles

  // Estado del formulario de creación/edición de proyectos
  showForm = signal(false);                  // Controla la visibilidad del modal de formulario
  editingId = signal<number | null>(null);   // ID del proyecto en edición (null = creación)
  formName = signal('');                     // Campo nombre del formulario
  formDescription = signal('');              // Campo descripción del formulario
  formTags = signal('');                     // Tecnologías separadas por comas
  formStatus = signal<Project['status']>('Producción'); // Estado del formulario
  saving = signal(false);                    // Indica si hay una operación en curso
  errorMessage = signal<string | null>(null); // Mensaje de error a mostrar

  // Estados posibles de un proyecto (para el selector del modal)
  statuses: Project['status'][] = ['Producción', 'En Desarrollo', 'Mantenimiento'];

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

  // Alterna la visibilidad del panel de filtros
  toggleFilters() {
    this.filtersExpanded.update(value => !value);
  }

  // Indica si hay algún filtro activo
  hasActiveFilters = computed(() =>
    !!this.searchTerm() || !!this.selectedStatus() || !!this.selectedFrontendTag() ||
    !!this.selectedBackendTag() || !!this.selectedDatabaseTag() || !!this.selectedCloudTag()
  );

  // Limpia todos los filtros
  clearFilters() {
    this.searchTerm.set('');
    this.selectedStatus.set('');
    this.selectedFrontendTag.set('');
    this.selectedBackendTag.set('');
    this.selectedDatabaseTag.set('');
    this.selectedCloudTag.set('');
  }

  // Abre el modal de detalles con el proyecto seleccionado
  openProject(proyecto: Project) {
    this.selectedProject.set(proyecto);
  }

  // Cierra el modal de detalles
  closeProject() {
    this.selectedProject.set(null);
  }

  // Actualiza el estado del proyecto seleccionado (persiste en el backend)
  updateStatus(status: Project['status']) {
    const selected = this.selectedProject();
    if (!selected || !this.auth.canEdit()) return;

    this.projectService.updateStatus(selected.id, status).subscribe({
      next: (updated) => {
        this.projects.update(projects =>
          projects.map(p => p.id === updated.id ? updated : p)
        );
        this.selectedProject.set(updated);
      },
      error: () => this.errorMessage.set('No se pudo actualizar el estado.')
    });
  }

  // Abre el formulario para crear un proyecto nuevo (solo ADMIN)
  openCreate() {
    this.editingId.set(null);
    this.formName.set('');
    this.formDescription.set('');
    this.formTags.set('');
    this.formStatus.set('Producción');
    this.errorMessage.set(null);
    this.showForm.set(true);
  }

  // Abre el formulario para editar un proyecto existente (ADMIN o TECNICO)
  openEdit(proyecto: Project) {
    this.editingId.set(proyecto.id);
    this.formName.set(proyecto.name);
    this.formDescription.set(proyecto.description);
    this.formTags.set(proyecto.tags.join(', '));
    this.formStatus.set(proyecto.status);
    this.errorMessage.set(null);
    this.showForm.set(true);
  }

  // Cierra el formulario
  closeForm() {
    this.showForm.set(false);
  }

  // Crea o actualiza el proyecto según el modo activo
  saveProject() {
    if (!this.formName().trim()) {
      this.errorMessage.set('El nombre es obligatorio.');
      return;
    }
    const request: ProjectRequest = {
      name: this.formName().trim(),
      description: this.formDescription().trim(),
      tags: this.formTags().split(',').map(t => t.trim()).filter(t => t.length > 0),
      status: this.formStatus()
    };

    this.saving.set(true);
    this.errorMessage.set(null);
    const id = this.editingId();
    const request$ = id
      ? this.projectService.update(id, request)
      : this.projectService.create(request);

    request$.subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.showForm.set(false);
        if (id) {
          this.projects.update(projects => projects.map(p => p.id === saved.id ? saved : p));
        } else {
          this.projects.update(projects => [...projects, saved]);
        }
      },
      error: () => {
        this.saving.set(false);
        this.errorMessage.set('No se pudo guardar el proyecto.');
      }
    });
  }

  // Elimina un proyecto tras confirmación (solo ADMIN)
  deleteProject(proyecto: Project) {
    if (!this.auth.canManage()) return;
    if (!confirm(`¿Eliminar el proyecto "${proyecto.name}"?`)) return;

    this.projectService.delete(proyecto.id).subscribe({
      next: () => {
        this.projects.update(projects => projects.filter(p => p.id !== proyecto.id));
        if (this.selectedProject()?.id === proyecto.id) {
          this.selectedProject.set(null);
        }
      },
      error: () => this.errorMessage.set('No se pudo eliminar el proyecto.')
    });
  }

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

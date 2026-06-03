/**
 * Servicio de proyectos conectado al backend (Spring Boot).
 *
 * Proporciona operaciones CRUD contra /api/projects. El control de acceso
 * por rol lo aplica el backend; el frontend solo muestra/oculta acciones.
 *
 * Este servicio está registrado como singleton a nivel de aplicación.
 */
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';
import { ProjectRequest } from '../models/project.request';
import { API_BASE_URL } from '../core/api.config';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  /** Cliente HTTP para realizar peticiones al backend */
  private http = inject(HttpClient);
  private base = `${API_BASE_URL}/projects`;

  /**
   * Obtiene la lista de todos los proyectos.
   *
   * @returns Observable con el array de proyectos
   */
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.base);
  }

  /** Crea un nuevo proyecto (requiere rol ADMIN en el backend). */
  create(project: ProjectRequest): Observable<Project> {
    return this.http.post<Project>(this.base, project);
  }

  /** Actualiza un proyecto completo (ADMIN o TECNICO). */
  update(id: number, project: ProjectRequest): Observable<Project> {
    return this.http.put<Project>(`${this.base}/${id}`, project);
  }

  /** Actualiza solo el estado de un proyecto (ADMIN o TECNICO). */
  updateStatus(id: number, status: Project['status']): Observable<Project> {
    return this.http.patch<Project>(`${this.base}/${id}/status`, { status });
  }

  /** Elimina un proyecto (requiere rol ADMIN en el backend). */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

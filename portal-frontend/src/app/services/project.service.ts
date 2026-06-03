/**
 * Servicio para obtener datos de proyectos.
 * 
 * Proporciona métodos para cargar la lista de proyectos desde el backend
 * o desde un archivo JSON local (actualmente usa /projects.json).
 * 
 * Este servicio está registrado como singleton a nivel de aplicación.
 */
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  /** Cliente HTTP para realizar peticiones al backend */
  private http = inject(HttpClient);

  /**
   * Obtiene la lista de todos los proyectos.
   * 
   * @returns Observable con el array de proyectos
   */
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>('/projects.json');
  }
}

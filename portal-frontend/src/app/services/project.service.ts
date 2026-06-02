// Importaciones de Angular y RxJS
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';

// Servicio para obtener datos de proyectos
@Injectable({
  providedIn: 'root'  // El servicio está disponible en toda la aplicación (singleton)
})
export class ProjectService {
  // Inyección de HttpClient para hacer peticiones HTTP
  private http = inject(HttpClient);

  // Método para obtener la lista de proyectos desde el archivo JSON
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>('/projects.json');  // Petición GET al archivo projects.json
  }
}

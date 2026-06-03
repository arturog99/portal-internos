/**
 * Servicio de gestión de usuarios (solo ADMIN).
 *
 * Proporciona operaciones CRUD sobre los usuarios del sistema
 * contra el backend (/api/users).
 */
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../core/api.config';
import { UserRequest, UserResponse } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${API_BASE_URL}/users`;

  /** Obtiene todos los usuarios. */
  getAll(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.base);
  }

  /** Crea un nuevo usuario. */
  create(user: UserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.base, user);
  }

  /** Actualiza un usuario existente. */
  update(id: number, user: UserRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.base}/${id}`, user);
  }

  /** Elimina un usuario. */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

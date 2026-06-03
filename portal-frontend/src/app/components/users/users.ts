/**
 * Componente de gestión de usuarios (solo ADMIN).
 *
 * Permite listar, crear, editar y eliminar usuarios del sistema.
 */
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Role, UserRequest, UserResponse } from '../../models/auth.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class UsersComponent {
  private userService = inject(UserService);

  users = signal<UserResponse[]>([]);
  roles: Role[] = ['ADMIN', 'TECNICO', 'VISITANTE'];

  /** Modal de creación/edición. */
  showForm = signal(false);
  editingId = signal<number | null>(null);
  form = signal<UserRequest>({ username: '', email: '', password: '', role: 'VISITANTE' });

  loading = signal(false);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  /** Carga la lista de usuarios. */
  load(): void {
    this.userService.getAll().subscribe({
      next: (users) => this.users.set(users),
      error: () => this.error.set('No se pudieron cargar los usuarios.'),
    });
  }

  /** Abre el formulario para crear un usuario nuevo. */
  openCreate(): void {
    this.editingId.set(null);
    this.form.set({ username: '', email: '', password: '', role: 'VISITANTE' });
    this.error.set(null);
    this.showForm.set(true);
  }

  /** Abre el formulario para editar un usuario existente. */
  openEdit(user: UserResponse): void {
    this.editingId.set(user.id);
    this.form.set({ username: user.username, email: user.email, password: '', role: user.role });
    this.error.set(null);
    this.showForm.set(true);
  }

  /** Cierra el formulario. */
  closeForm(): void {
    this.showForm.set(false);
  }

  /** Actualiza un campo del formulario. */
  patchForm<K extends keyof UserRequest>(key: K, value: UserRequest[K]): void {
    this.form.update((f) => ({ ...f, [key]: value }));
  }

  /** Crea o actualiza el usuario según el modo. */
  save(): void {
    const data = this.form();
    if (!data.username || !data.email || (!this.editingId() && !data.password)) {
      this.error.set('Completa usuario, email y contraseña.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);

    const id = this.editingId();
    const request$ = id
      ? this.userService.update(id, data)
      : this.userService.create(data);

    request$.subscribe({
      next: () => {
        this.loading.set(false);
        this.showForm.set(false);
        this.load();
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(
          err.status === 409 || err.status === 400
            ? 'Datos inválidos o usuario/email ya existe.'
            : 'No se pudo guardar el usuario.'
        );
      },
    });
  }

  /** Elimina un usuario tras confirmación. */
  remove(user: UserResponse): void {
    if (!confirm(`¿Eliminar al usuario "${user.username}"?`)) return;
    this.userService.delete(user.id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('No se pudo eliminar el usuario.'),
    });
  }
}

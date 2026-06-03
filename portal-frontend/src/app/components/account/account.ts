/**
 * Componente de cuenta del usuario.
 *
 * Muestra el perfil y permite configurar la autenticación de dos factores (2FA):
 * 1. Generar el QR (setup).
 * 2. Escanearlo con Google Authenticator.
 * 3. Introducir el primer código para activarlo (enable).
 */
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { TotpSetup, UserResponse } from '../../models/auth.model';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './account.html',
  styleUrl: './account.css',
})
export class AccountComponent {
  private accountService = inject(AccountService);
  protected auth = inject(AuthService);

  profile = signal<UserResponse | null>(null);
  setup = signal<TotpSetup | null>(null);
  code = signal('');

  loading = signal(false);
  message = signal<string | null>(null);
  error = signal<string | null>(null);

  constructor() {
    this.loadProfile();
  }

  /** Carga el perfil del usuario autenticado. */
  loadProfile(): void {
    this.accountService.me().subscribe({
      next: (user) => this.profile.set(user),
      error: () => this.error.set('No se pudo cargar el perfil.'),
    });
  }

  /** Inicia la configuración 2FA generando el QR. */
  startSetup(): void {
    this.loading.set(true);
    this.error.set(null);
    this.message.set(null);
    this.accountService.setupTotp().subscribe({
      next: (data) => {
        this.setup.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No se pudo generar el código QR.');
      },
    });
  }

  /** Activa el 2FA verificando el primer código. */
  enable(): void {
    if (!this.code()) {
      this.error.set('Introduce el código de 6 dígitos.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.accountService.enableTotp(this.code()).subscribe({
      next: () => {
        this.loading.set(false);
        this.message.set('2FA activado correctamente.');
        this.setup.set(null);
        this.code.set('');
        this.loadProfile();
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Código incorrecto. Inténtalo de nuevo.');
      },
    });
  }
}

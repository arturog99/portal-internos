/**
 * Componente de inicio de sesión.
 *
 * Gestiona el flujo de login en dos pasos:
 * 1. Usuario y contraseña.
 * 2. Si el backend lo requiere, verificación del código 2FA (TOTP).
 *
 * Tras un login correcto, redirige al catálogo de proyectos.
 */
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  /** Credenciales del primer paso. */
  username = signal('');
  password = signal('');

  /** Datos del segundo paso (2FA). */
  twoFactorRequired = signal(false);
  tempToken = signal<string | null>(null);
  code = signal('');

  /** Estado de la UI. */
  loading = signal(false);
  error = signal<string | null>(null);

  /** Paso 1: valida usuario y contraseña. */
  submitLogin(): void {
    if (!this.username() || !this.password()) {
      this.error.set('Introduce usuario y contraseña.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.username(), this.password()).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.twoFactorRequired) {
          this.twoFactorRequired.set(true);
          this.tempToken.set(res.tempToken);
        } else {
          this.router.navigate(['/proyectos']);
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(
          err.status === 401 || err.status === 403
            ? 'Usuario o contraseña incorrectos.'
            : 'No se pudo conectar con el servidor.'
        );
      },
    });
  }

  /** Paso 2: verifica el código 2FA. */
  submitTwoFactor(): void {
    const token = this.tempToken();
    if (!token || !this.code()) {
      this.error.set('Introduce el código de 6 dígitos.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);

    this.auth.verifyTwoFactor(token, this.code()).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/proyectos']);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Código 2FA incorrecto o expirado.');
      },
    });
  }

  /** Login mediante certificado digital (X.509 / mTLS). */
  loginWithCertificate(): void {
    this.loading.set(true);
    this.error.set(null);

    this.auth.certLogin().subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/proyectos']);
      },
      error: (err) => {
        this.loading.set(false);
        if (err.status === 401) {
          this.error.set('No se detectó un certificado válido. Asegúrate de seleccionarlo en el navegador.');
        } else {
          this.error.set('No se pudo conectar con el servidor de certificados (HTTPS 8443). Revisa que el backend esté en modo certificado y que confías en la CA.');
        }
      },
    });
  }

  /** Vuelve al paso de credenciales. */
  cancelTwoFactor(): void {
    this.twoFactorRequired.set(false);
    this.tempToken.set(null);
    this.code.set('');
    this.error.set(null);
  }
}

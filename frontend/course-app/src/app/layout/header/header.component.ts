import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { environment } from '../../../environments/environment';
import { Role } from '../../shared/core/role.enum';
import { URL_LOGIN } from '../../shared/core/urls';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  private authService = inject(AuthService);

  logoutUrl: string = `${environment.userBackendHost}/logout`;

  isAuthenticated() {
    return !!this.authService.profile;
  }

  canViewUsers() {
    return this.authService.isAdmin() || this.authService.isTeacher();
  }

  login(): void {
    window.location.href = URL_LOGIN;
  }

  logout(): void {
    const form = document.getElementById('logoutForm') as HTMLFormElement;
    form?.submit(); // ✅ Spring handles the logout, redirect, and session invalidation
  }
}

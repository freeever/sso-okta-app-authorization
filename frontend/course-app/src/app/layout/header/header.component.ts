import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { environment } from '../../../environments/environment';
import { LOGIN_URL } from '../../shared/constants';
import { Role } from '../../shared/core/role.enum';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  logoutUrl: string = `${environment.userBackendHost}/logout`;

  constructor(private authService: AuthService) {}

  isAuthenticated() {
    return !!this.authService.profile;
  }

  isAdmin() {
    return this.authService.profile?.role === Role.ADMIN;
  }

  login(): void {
    window.location.href = LOGIN_URL;
  }

  logout(): void {
    const form = document.getElementById('logoutForm') as HTMLFormElement;
    form?.submit(); // ✅ Spring handles the logout, redirect, and session invalidation
  }
}

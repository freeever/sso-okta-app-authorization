import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService, Role } from 'shared-lib';
import { environment } from '../../../environments/environment';
import { URL_LOGIN, URL_LOGOUT } from '../../core/urls';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  private authService = inject(AuthService);

  logoutUrl: string = URL_LOGOUT;

  isAuthenticated() {
    return !!this.authService.profile;
  }

  isAdmin() {
    return this.authService.profile?.role === Role.ADMIN;
  }

  gotoCourseManagement() {
    window.location.href = "http://localhost:4201";
  }

  login(): void {
    window.location.href = URL_LOGIN;
  }

  logout(): void {
    const form = document.getElementById('logoutForm') as HTMLFormElement;
    form?.submit(); // ✅ Spring handles the logout, redirect, and session invalidation
  }
}

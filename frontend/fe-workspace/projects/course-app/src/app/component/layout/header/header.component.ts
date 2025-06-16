import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { URL_LOGIN, URL_LOGOUT } from '../../../core/urls';
import { AuthService } from 'shared-lib';

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

  gotoUserManagement() {
    window.location.href = "http://localhost:4200";
  }

  login(): void {
    window.location.href = URL_LOGIN;
  }

  logout(): void {
    const form = document.getElementById('logoutForm') as HTMLFormElement;
    form?.submit(); // ✅ Spring handles the logout, redirect, and session invalidation
  }
}

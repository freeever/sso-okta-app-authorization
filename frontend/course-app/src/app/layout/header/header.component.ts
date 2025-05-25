import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { environment } from '../../../environments/environment';
import { LOGIN_URL } from '../../shared/constants';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  logoutUrl: string = `${environment.userBackendHost}/logout`
  authService = inject(AuthService);

  login(): void {
    window.location.href = LOGIN_URL;
  }

  logout(): void {
    const form = document.getElementById('logoutForm') as HTMLFormElement;
    form?.submit(); // ✅ Spring handles the logout, redirect, and session invalidation
  }
}

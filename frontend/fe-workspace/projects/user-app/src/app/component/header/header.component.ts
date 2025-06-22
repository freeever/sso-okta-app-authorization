import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { AuthService } from 'shared-lib';
import { URL_LOGIN, URL_LOGOUT } from '../../core/urls';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit, OnDestroy {

  private authService = inject(AuthService);

  logoutUrl: string = URL_LOGOUT;

  private destroy$ = new Subject<void>();
  isAuthenticated$ = this.authService.isAuthenticated$;

  ngOnInit(): void {
    this.authService.checkAuthentication()
      .pipe(takeUntil(this.destroy$))
      .subscribe();
  }

  canViewUsers() {
    return this.authService.isAdmin() || this.authService.isTeacher();
  }

  redirectToCourseApp(targetPath: string): string {
    const encodedRedirectTo = encodeURIComponent(targetPath);
    return `http://localhost:9001/custom-login/course-app?redirectTo=${encodedRedirectTo}`;
  }

  login(): void {
    window.location.href = URL_LOGIN;
  }

  logout(): void {
    const form = document.getElementById('logoutForm') as HTMLFormElement;
    form?.submit(); // ✅ Spring handles the logout, redirect, and session invalidation
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

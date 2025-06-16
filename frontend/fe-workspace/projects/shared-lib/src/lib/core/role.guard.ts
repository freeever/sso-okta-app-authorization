import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../service/auth.service';

export const RoleGurad: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  const allowedRoles: string[] = route.data['roles'] || [];
  const userRole = authService.profile?.role;

  if (!userRole || !allowedRoles.includes(userRole)) {
    snackBar.open('🚫 You are not authorized to access this page.', 'Dismiss', {
      duration: 5000,
      verticalPosition: 'top',
      panelClass: 'mat-mdc-snack-bar-warn'
    });

    router.navigate(['/']);
    return false;
  }

  return true;
}

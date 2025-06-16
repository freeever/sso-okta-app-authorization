import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { NotificationService } from '../service/notification.service';

export const RoleGurad: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const notificationService = inject(NotificationService)
  const router = inject(Router);

  const allowedRoles: string[] = route.data['roles'] || [];
  const userRole = authService.profile?.role;

  if (!userRole || !allowedRoles.includes(userRole)) {
    notificationService.error('🚫 You are not authorized to access this page.');

    router.navigate(['/']);
    return false;
  }

  return true;
}

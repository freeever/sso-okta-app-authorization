import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthService } from '../../service/auth.service';
import { LOGIN_URL } from '../constants';

export const AuthGuard: CanActivateFn = () => {
  const authService = inject(AuthService);

  return authService.isAuthenticated$.pipe(
    map(isAuth => {
      if (!isAuth) {
        window.location.href = LOGIN_URL;
        return false;
      }
      return true;
    })
  );
};

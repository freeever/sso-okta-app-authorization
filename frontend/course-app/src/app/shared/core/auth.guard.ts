import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthService } from '../../service/auth.service';
import { URL_LOGIN } from './urls';

export const AuthGuard: CanActivateFn = () => {
  const authService = inject(AuthService);

  return authService.isAuthenticated$.pipe(
    map(isAuth => {
      if (!isAuth) {
        window.location.href = URL_LOGIN;
        return false;
      }
      return true;
    })
  );
};

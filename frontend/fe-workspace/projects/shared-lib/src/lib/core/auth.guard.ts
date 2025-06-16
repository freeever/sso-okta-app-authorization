import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthService } from '../service/auth.service';
import { SHARED_LIB_CONFIG } from '../config/shared-lib-config.token';
import { SharedLibConfig } from '../config/shared-lib-config.interface';

export const AuthGuard: CanActivateFn = () => {

  const sharedLibConfig: SharedLibConfig = inject(SHARED_LIB_CONFIG);
  const authService = inject(AuthService);

  return authService.isAuthenticated$.pipe(
    map(isAuth => {
      if (!isAuth) {
        window.location.href = sharedLibConfig.loginUrl;
        return false;
      }
      return true;
    })
  );
};

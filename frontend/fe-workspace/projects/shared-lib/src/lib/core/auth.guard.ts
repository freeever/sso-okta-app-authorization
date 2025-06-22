import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { tap } from 'rxjs/operators';
import { AuthService } from '../service/auth.service';
import { SHARED_LIB_CONFIG } from '../config/shared-lib-config.token';
import { SharedLibConfig } from '../config/shared-lib-config.interface';

export const AuthGuard: CanActivateFn = () => {

  const sharedLibConfig: SharedLibConfig = inject(SHARED_LIB_CONFIG);
  const authService = inject(AuthService);

  return authService.checkAuthentication().pipe(
    tap(isAuthenticated => {
      if (!isAuthenticated) {
        window.location.href = sharedLibConfig.loginUrl;
      }
    })
  );
};

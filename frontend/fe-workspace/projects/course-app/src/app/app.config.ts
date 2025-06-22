import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { BrowserAnimationsModule, provideAnimations } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatNativeDateModule } from '@angular/material/core';

import { routes } from './app.routes';
import { AppInterceptor, DateFormatProvider, SHARED_LIB_CONFIG, SharedLibConfig } from 'shared-lib';
import { URL_LOGIN, URL_PROFILE_API, URL_USERS_API } from './core/urls';

const sharedLibConfig: SharedLibConfig = {
  loginUrl: URL_LOGIN,
  profileApiUrl: URL_PROFILE_API,
  usersApiUrl: URL_USERS_API
}

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(withInterceptorsFromDi()), // ✅ This fixes HttpClient injection
    provideRouter(routes),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AppInterceptor,
      multi: true
    },
    importProvidersFrom(
      BrowserAnimationsModule,
      MatSnackBarModule
    ),
    provideAnimations(),
    importProvidersFrom(MatNativeDateModule),
    DateFormatProvider,
    // Inject config to the shared lib
    { provide: SHARED_LIB_CONFIG, useValue: sharedLibConfig }
  ]
};

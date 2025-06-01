import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { BrowserAnimationsModule, provideAnimations } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { routes } from './app.routes';
import { AppInterceptor } from './shared/core/app.interceptor';
import { DateFormatProvider } from './shared/core/date-format.provider';
import { MatNativeDateModule } from '@angular/material/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
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
    provideRouter(routes),
    provideAnimations(),
    importProvidersFrom(MatNativeDateModule),
    DateFormatProvider
  ],
};

import { Injectable, inject } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { NotificationService } from '../../service/notification.service';

@Injectable()
export class AppInterceptor implements HttpInterceptor {
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // ✅ This for logged in user who does not have profile
          this.notificationService.error('🚫 You do not have profile on our site.');
          // ✅ No profile. Go to dashboard page
          this.router.navigate(['']);
        } else if (error.status === 403) {
          // ✅ Not authorized — show snackbar
          this.notificationService.error('🚫 You are not authorized to access this resource.');
          // ✅ No profile. Go to dashboard page
          this.router.navigate(['']);
        }

        return throwError(() => error);
      })
    );
  }
}

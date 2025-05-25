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
import { MatSnackBar } from '@angular/material/snack-bar';
import { LOGIN_URL } from '../constants';

@Injectable()
export class AppInterceptor implements HttpInterceptor {
  private snackBar = inject(MatSnackBar);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // ✅ Not authenticated — redirect to login
          window.location.href = LOGIN_URL;
        } else if (error.status === 403) {
          // ✅ Not authorized — show snackbar
          this.snackBar.open('🚫 You are not authorized to access this resource.', 'Dismiss', {
            duration: 5000,
            verticalPosition: 'top',
            panelClass: 'mat-mdc-snack-bar-warn'
          });
        }

        return throwError(() => error);
      })
    );
  }
}

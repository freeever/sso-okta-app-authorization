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
import { URL_LOGIN } from './urls';
import { Router } from '@angular/router';

@Injectable()
export class AppInterceptor implements HttpInterceptor {
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // ✅ This for logged in user who does not have profile
          this.snackBar.open('🚫 You do not have profile on our site.', 'Dismiss', {
            duration: 5000,
            verticalPosition: 'top',
            panelClass: 'mat-mdc-snack-bar-warn'
          });
          // ✅ No profile. Go to dashboard page
          this.router.navigate(['']);
        } else if (error.status === 403) {
          // ✅ Not authorized — show snackbar
          this.snackBar.open('🚫 You are not authorized to access this resource.', 'Dismiss', {
            duration: 5000,
            verticalPosition: 'top',
            panelClass: 'mat-mdc-snack-bar-warn'
          });
          // ✅ No profile. Go to dashboard page
          this.router.navigate(['']);
        }

        return throwError(() => error);
      })
    );
  }
}

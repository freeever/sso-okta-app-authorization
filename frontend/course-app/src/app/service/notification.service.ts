import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {}

  success(message: string): void {
    this.snackBar.open(`✅ ${message}`, 'Dismiss', {
      duration: 3000,
      verticalPosition: 'top',
      panelClass: ['mat-mdc-snack-bar-success']
    });
  }

  error(message: string): void {
    this.snackBar.open(`🚫 ${message}`, 'Dismiss', {
      duration: 5000,
      verticalPosition: 'top',
      panelClass: ['mat-mdc-snack-bar-error']
    });
  }

  warning(message: string): void {
    this.snackBar.open(`⚠️ ${message}`, 'Dismiss', {
      duration: 5000,
      verticalPosition: 'top',
      panelClass: ['mat-mdc-snack-bar-warn']
    });
  }

  info(message: string): void {
    this.snackBar.open(`ℹ️ ${message}`, 'Dismiss', {
      duration: 4000,
      verticalPosition: 'top',
      panelClass: ['mat-mdc-snack-bar-info']
    });
  }
}

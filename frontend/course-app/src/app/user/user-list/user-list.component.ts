import { Router } from '@angular/router';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { User } from '../../model/user.model';
import { UserAdminService } from '../../service/user-admin.service';
import { MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConfirmDialogComponent } from '../../shared/component/confirm-dialog/confirm-dialog.component';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinner
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
})
export class UserListComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  users: User[] = [];
  displayedColumns = ['firstName', 'lastName', 'email', 'role', 'actions'];
  isLoading = true;

  constructor(
    private router: Router,
    private adminService: UserAdminService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: () => this.snackBar.open('❌ Failed to load users', 'Dismiss', { duration: 3000 })
    });
  }
  viewUser(id: number): void {
    this.router.navigate(['/users', id]);
  }

  openDeleteDialog(user: any): void {
    this.dialog.open(ConfirmDialogComponent, {
      width: '600px',
      data: {
        title: 'Confirm Delete',
        contentHtml: `Are you sure you want to delete user <strong>${user.lastName}, ${user.firstName}</strong>?`,
        confirmButtonLabel: 'Delete',
        cancelButtonLabel: 'Cancel',
        showCancelButton: true,
        onConfirm: () => this.deleteUser(user.id)
      }
    });
  }

  private deleteUser(id: number) {
    this.adminService.deleteUser(id).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: () => {
        this.snackBar.open('✅ User deleted', 'Dismiss', { duration: 3000 });
        this.loadUsers();
      },
      error: () => {
        this.snackBar.open('❌ Failed to delete user', 'Dismiss', { duration: 3000 });
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

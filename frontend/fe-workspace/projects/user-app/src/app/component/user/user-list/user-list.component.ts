import { Router } from '@angular/router';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { Subject, takeUntil } from 'rxjs';
import { ConfirmDialogComponent, NotificationService, User, UserAdminService } from 'shared-lib';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
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
    private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: () => this.notificationService.error('Failed to load users')
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
        this.notificationService.success('User deleted');
        this.loadUsers();
      },
      error: () => {
        this.notificationService.error('Failed to delete user');
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

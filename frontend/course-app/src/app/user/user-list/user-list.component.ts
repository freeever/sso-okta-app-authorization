import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { User } from '../../model/user.model';
import { UserAdminService } from '../../service/user-admin.service';
import { MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserDeleteDialogComponent } from '../user-delete-dialog.component';

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
export class UserListComponent implements OnInit {

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
    this.router.navigate(['/admin/users', id]);
  }

  openDeleteDialog(user: any): void {
    const dialogRef = this.dialog.open(UserDeleteDialogComponent, {
      width: '400px',
      data: { id: user.id, firstName: user.firstName, lastName: user.lastName }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.adminService.deleteUser(user.id).subscribe({
          next: () => {
            this.snackBar.open('✅ User deleted', 'Dismiss', { duration: 3000 });
            this.loadUsers();
          },
          error: () => {
            this.snackBar.open('❌ Failed to delete user', 'Dismiss', { duration: 3000 });
          }
        });
      }
    });
  }
}

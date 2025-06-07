import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { User } from '../../model/user.model';
import { UserAdminService } from '../../service/user-admin.service';
import { MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
})
export class UserListComponent implements OnInit {

  users: User[] = [];
  displayedColumns = ['firstName', 'lastName', 'email', 'role', 'actions'];

  constructor(
    private router: Router,
    private adminService: UserAdminService) {}

  ngOnInit(): void {
    this.adminService.getAllUsers().subscribe(users => {
      this.users = users;
      console.log(this.users);
    });
  }

    viewUser(id: number): void {
    this.router.navigate(['/admin/users', id]);
  }
}

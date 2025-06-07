import { Component, OnInit } from '@angular/core';
import { User } from '../../model/user.model';
import { UserAdminService } from '../../service/user-admin.service';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
})
export class UserListComponent implements OnInit {

  users: User[] = [];

  constructor(private userAdminService: UserAdminService) {}

  ngOnInit(): void {
    this.userAdminService.getAllUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Failed to load users', err)
    });
  }
}

import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {

   private http = inject(HttpClient);

    ngOnInit(): void {
    this.http.get<any>('/api/admin/users', { withCredentials: true }).subscribe({
      next: users => console.log(users),
      error: err => console.error('Failed to load profile', err)
    });
  }
}

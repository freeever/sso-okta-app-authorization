import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-user-profile',
    standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);

  form = this.fb.group({
    firstName: [''],
    lastName: [''],
    gender: [''],
    role: ['']
  });

  ngOnInit(): void {
    this.http.get<any>('/api/profile/me', { withCredentials: true }).subscribe({
      next: user => this.form.patchValue(user),
      error: err => console.error('Failed to load profile', err)
    });
  }

  submit(): void {
    this.http.put<any>('/api/profile/me', this.form.value, { withCredentials: true }).subscribe({
      next: () => alert('Profile updated successfully'),
      error: err => console.error('Failed to update profile', err)
    });
  }
}

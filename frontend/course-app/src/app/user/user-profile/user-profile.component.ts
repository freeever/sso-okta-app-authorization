import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { format } from 'date-fns';

import { UserForm } from '../../model/user-form.model';
import { ProfileService } from './../../service/profile.service';
import { NotificationService } from '../../service/notification.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatSnackBarModule,
    MatCardModule
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit, OnDestroy {

  private profileService = inject(ProfileService);
  private notification = inject(NotificationService);

  private destroy$ = new Subject<void>();
  private profileData: any;
  isEditMode: boolean = false;

  form!: FormGroup;

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.profileService.getProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: profile => {
          this.profileData = profile;
          this.form = new UserForm(profile).toForm();
        },
        error: err => this.notification.error('Failed to load profile')
      });
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const profile = new UserForm().toModel(this.form);
      const payload = {
        ...profile,
        dateOfBirth: profile.dateOfBirth ? format(profile.dateOfBirth, 'yyyy-MM-dd') : null
      };

      this.profileService.updateProfile(payload).subscribe({
        next: profile => {
          this.profileData = profile;
          this.toggleEdit();
          this.notification.success('Profile updated successfully');
        },
        error: err => this.notification.error('Failed to update profile')
      })
    }
  }

  toggleEdit() {
    if (this.isEditMode) {
      this.form = new UserForm(this.profileData).toForm();
    }
    this.isEditMode = !this.isEditMode;
  }

  cancelEdit() {
    this.form.markAsPristine();
    this.isEditMode = false;
    this.form.patchValue(this.profileData); // Restore previous values
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get oktaUserId() {
    return this.form?.get('oktaUserId')
  }
  get firstName() {
    return this.form?.get('firstName')
  }
  get lastName() {
    return this.form?.get('lastName')
  }
  get email() {
    return this.form?.get('email')
  }
  get gender() {
    return this.form?.get('gender')
  }
  get dateOfBirth() {
    return this.form?.get('dateOfBirth')
  }
  get role() {
    return this.form?.get('role')
  }
}

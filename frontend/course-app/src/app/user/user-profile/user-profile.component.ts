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
import { DateAdapter, MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { format } from 'date-fns';

import { User } from '../../model/user.model';
import { ProfileService } from './../../service/profile.service';
import { NotificationService } from '../../service/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserAdminService } from '../../service/user-admin.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../service/auth.service';

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
    MatCardModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit, OnDestroy {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private profileService = inject(ProfileService);
  private adminService = inject(UserAdminService);
  private authService = inject(AuthService);
  private notificationService = inject(NotificationService);
  private dateAdapter = inject(DateAdapter<Date>);

  private destroy$ = new Subject<void>();
  private profileData!: User;

  userId?: number;
  isEditMode: boolean = false;

  form!: FormGroup;

  ngOnInit(): void {
    this.dateAdapter.setLocale('en-CA'); // ensures yyyy-MM-dd format

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.userId = +id;
        // Fetch user's profile by admin
        this.loadUser(+id);
      } else {
        // Load self profile
        this.loadSelf();
      }
    });
  }

  loadUser(id: number): void {
    this.adminService.getUserById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: user => {
          this.profileData = user;
          this.form = new User(user).toForm();
        },
        error: () => {
          this.notificationService.error('Failed to load user information');
          this.router.navigate(['/users']);
        }
      });
  }

  loadSelf(): void {
    this.profileService.getProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: profile => {
          this.profileData = profile;
          this.form = new User(profile).toForm();
        },
        error: () => {
          this.notificationService.error('Failed to load profile');
          this.router.navigate(['/users']);
        }
      });
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const profile = new User().toModel(this.form);
      const payload = {
        ...profile,
        dateOfBirth: profile.dateOfBirth ? format(profile.dateOfBirth, 'yyyy-MM-dd') : null
      };

      if (this.userId) {
        this.adminService.updateUserByAdmin(this.userId, payload).subscribe({
          next: profile => {
            this.profileData = profile;
            this.toggleEdit();
            this.notificationService.success('User information updated successfully');
          },
          error: err => this.notificationService.error('Failed to update profile')
        })
      } else {
        this.profileService.updateProfile(payload).subscribe({
          next: profile => {
            this.profileData = profile;
            this.toggleEdit();
            this.notificationService.success('Profile updated successfully');
          },
          error: err => this.notificationService.error('Failed to update profile')
        })
      }
    }
  }

  toggleEdit() {
    if (this.isEditMode) {
      this.form = new User(this.profileData).toForm();
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

import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import { ProfileService } from './profile.service';
import { Role } from '../core/role.enum';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private authenticated = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.authenticated.asObservable();

  private _profile!: User | null;

  public get profile() {
    return this._profile;
  }

  constructor(private profileService: ProfileService, private router: Router) {
  }

  checkAuthentication(): void {
    this.profileService.getProfile().subscribe({
      next: (user) => {
        this._profile = user;
        this.authenticated.next(true);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404) {
          // Profile not found → try creating it
          this.profileService.createProfile().subscribe({
            next: (createdUser) => {
              this._profile = createdUser;
              this.authenticated.next(true);
              this.router.navigate(['/']); // redirect to home after profile creation
            },
            error: (err) => {
              this._profile = null;
              this.authenticated.next(false);
              console.error('❌ Failed to create profile', err);
              this.router.navigate(['/']); // still redirect to home
            }
          });
        } else {
          this._profile = null;
          this.authenticated.next(false);
          console.error('❌ Failed to load profile', error);
        }
      }
    });
  }

  isAdmin() {
    return this._profile?.role === Role.ADMIN.toString();
  }

  isStudent() {
    return this._profile?.role === Role.STUDENT.toString();
  }

  isTeacher() {
    return this._profile?.role === Role.TEACHER.toString();
  }
}

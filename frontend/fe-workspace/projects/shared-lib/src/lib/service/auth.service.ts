import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, finalize, map, mapTo, Observable, of, shareReplay, tap } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { User } from '../model/user.model';
import { ProfileService } from './profile.service';
import { Role } from '../core/role.enum';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private authenticated = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.authenticated.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(true);
  public loading$ = this.loadingSubject.asObservable();

  private _profile!: User | null;

  public get profile() {
    return this._profile;
  }

  constructor(private profileService: ProfileService) {}

  private checkInProgress$: Observable<boolean> | null = null;

  checkAuthentication(): Observable<boolean> {
    // ✅ If already authenticated, return true immediately
    if (this.authenticated.value === true) {
      return of(true);
    }

    // ✅ If already checking, return the same observable
    if (this.checkInProgress$) {
      return this.checkInProgress$;
    }

    // 🔄 Otherwise, perform check and cache it
    const check$ = this.profileService.getProfile().pipe(
      tap(user => {
        this._profile = user;
        this.authenticated.next(true);
      }),
      map(() => true),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          // Try creating profile
          return this.profileService.createProfile().pipe(
            tap(createdUser => {
              this._profile = createdUser;
              this.authenticated.next(true);
            }),
            map(() => true),
              catchError(err => {
              console.error('❌ Failed to create profile', err);
              this._profile = null;
              this.authenticated.next(false);
              return of(false);
            })
          );
        } else {
          console.error('❌ Failed to load profile', error);
          this._profile = null;
          this.authenticated.next(false);
          return of(false);
        }
      }),
      finalize(() => {
        // 🚫 Clear cache after first run completes
        this.checkInProgress$ = null;
        this.loadingSubject.next(false); // 👈 done loading
      }),
      shareReplay(1) // 🔁 Reuse the result for all subscribers
    );

    this.checkInProgress$ = check$;
    return check$;
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

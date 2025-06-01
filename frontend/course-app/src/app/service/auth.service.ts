import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { URL_PROFILE } from '../shared/core/urls';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private authenticated = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.authenticated.asObservable();

  private _profile: any = null;

  public get profile() {
    return this._profile;
  }

  constructor(private http: HttpClient, private router: Router) {
  }

  checkAuthentication(): void {
    this.http.get<any>(URL_PROFILE, {
      withCredentials: true // ✅ include session cookie!
    }).subscribe({
      next: (user) => {
        this._profile = user;
        this.authenticated.next(true);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404) {
          // No user profile yet — create it
          this.http.post(URL_PROFILE, {}, { withCredentials: true }).subscribe({
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
}

import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private authenticated = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.authenticated.asObservable();

  private _profile: any = null;

  public get profile() {
    return this._profile;
  }

  constructor(private http: HttpClient) {
  }

  checkAuthentication(): void {
    this.http.get<any>('/api/profile/me', {
      withCredentials: true // ✅ include session cookie!
    }).subscribe({
      next: (user) => {
        this._profile = user;
        this.authenticated.next(true);
      },
      error: () => {
        this._profile = null;
        this.authenticated.next(false);
      }
    });
  }
}

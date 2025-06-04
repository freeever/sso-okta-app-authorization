import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { URL_PROFILE } from '../shared/core/urls';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private http = inject(HttpClient);

  getProfile(): Observable<User> {
    return this.http.get<User>(URL_PROFILE, { withCredentials: true });
  }

  createProfile(): Observable<User> {
    return this.http.post<User>(URL_PROFILE, null, { withCredentials: true });
  }

  updateProfile(user: any): Observable<any> {
    return this.http.put<User>(URL_PROFILE, user, { withCredentials: true });
  }
}

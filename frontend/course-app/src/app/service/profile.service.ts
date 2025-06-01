import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { URL_PROFILE } from '../shared/core/urls';
import { UserForm } from '../model/user-form.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private http = inject(HttpClient);

  getProfile(): Observable<any> {
    return this.http.get<any>(URL_PROFILE, { withCredentials: true });
  }

  createProfile(): Observable<any> {
    return this.http.post<any>(URL_PROFILE, null, { withCredentials: true });
  }

  updateProfile(user: any): Observable<any> {
    return this.http.put<UserForm>(URL_PROFILE, user, { withCredentials: true });
  }
}

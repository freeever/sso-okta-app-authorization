import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { URL_PROFILE, URL_USERS } from '../shared/core/urls';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserAdminService {
  private http = inject(HttpClient);

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(URL_USERS, { withCredentials: true });
  }
}

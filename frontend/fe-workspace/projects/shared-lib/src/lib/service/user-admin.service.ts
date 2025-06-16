import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SharedLibConfig } from '../config/shared-lib-config.interface';
import { User } from '../model/user.model';
import { SHARED_LIB_CONFIG } from '../config/shared-lib-config.token';

@Injectable({
  providedIn: 'root'
})
export class UserAdminService {
  private http = inject(HttpClient);
  private sharedLibConfig: SharedLibConfig = inject(SHARED_LIB_CONFIG);

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.sharedLibConfig.usersApiUrl, { withCredentials: true });
  }

  getUserById(id: number): Observable<User> {
  return this.http.get<User>(`/api/admin/users/${id}`);
}

updateUserByAdmin(id: number, user: any): Observable<User> {
  return this.http.put<User>(`/api/admin/users/${id}`, user);
}

deleteUser(id: number): Observable<any> {
  return this.http.delete(`/api/admin/users/${id}`);
}
}

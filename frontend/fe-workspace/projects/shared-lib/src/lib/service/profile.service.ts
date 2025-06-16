import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';
import { SharedLibConfig } from '../config/shared-lib-config.interface';
import { SHARED_LIB_CONFIG } from '../config/shared-lib-config.token';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private sharedLibConfig: SharedLibConfig = inject(SHARED_LIB_CONFIG);
  private http = inject(HttpClient);

  getProfile(): Observable<User> {
    return this.http.get<User>(this.sharedLibConfig.profileApiUrl, { withCredentials: true });
  }

  createProfile(): Observable<User> {
    return this.http.post<User>(this.sharedLibConfig.profileApiUrl, null, { withCredentials: true });
  }

  updateProfile(user: any): Observable<any> {
    return this.http.put<User>(this.sharedLibConfig.profileApiUrl, user, { withCredentials: true });
  }
}

import { Routes } from '@angular/router';
import { DashboardComponent } from './component/dashboard/dashboard.component';
import { AuthGuard, RoleGurad, UserProfileComponent } from 'shared-lib';
import { UserListComponent } from './component/user/user-list/user-list.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'profile', component: UserProfileComponent, canActivate: [ AuthGuard ] },
  { path: 'users', component: UserListComponent, canActivate: [ AuthGuard, RoleGurad ], data: { roles: ['ADMIN', "TEACHER"]} },
  { path: 'users/:id', component: UserProfileComponent, canActivate: [ AuthGuard, RoleGurad ], data: { roles: ['ADMIN', "TEACHER"]} }
];

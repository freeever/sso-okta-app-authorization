import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserListComponent } from './user/user-list/user-list.component';
import { CourseListComponent } from './course/course-list/course-list.component';
import { CourseDetailsComponent } from './course/course-details/course-details.component';
import { AuthGuard } from './shared/core/auth.guard';
import { UserProfileComponent } from './user/user-profile/user-profile.component';
import { RoleGurad } from './shared/core/role.guard';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  // { path: 'profile', loadComponent: () => import('./user/user-profile/user-profile.component').then(m => m.UserProfileComponent), canActivate: [AuthGuard] },
  { path: 'profile', component: UserProfileComponent, canActivate: [ AuthGuard ] },
  { path: 'users', component: UserListComponent, canActivate: [ AuthGuard, RoleGurad ], data: { roles: ['ADMIN']} },
  { path: 'users/:id', component: UserProfileComponent, canActivate: [ AuthGuard, RoleGurad ], data: { roles: ['ADMIN', "TEACHER"]} },
  { path: 'courses', component: CourseListComponent, canActivate: [ AuthGuard ] },
  { path: 'courses/:id', component: CourseDetailsComponent, canActivate: [ AuthGuard ] }
];

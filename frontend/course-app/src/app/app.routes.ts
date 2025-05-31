import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserListComponent } from './user/user-list/user-list.component';
import { EditUserComponent } from './user/edit-user/edit-user.component';
import { CourseListComponent } from './course/course-list/course-list.component';
import { CourseManageComponent } from './course/course-manage/course-manage.component';
import { RegisteredCoursesComponent } from './course/registered-courses/registered-courses.component';
import { AuthGuard } from './shared/core/auth.guard';
import { UserProfileComponent } from './user/user-profile/user-profile.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  // { path: 'profile', loadComponent: () => import('./user/user-profile/user-profile.component').then(m => m.UserProfileComponent), canActivate: [AuthGuard] },
  { path: 'profile', component: UserProfileComponent, canActivate: [AuthGuard] },
  { path: 'admin/users', component: UserListComponent },
  { path: 'users/edit/:email', component: EditUserComponent },
  { path: 'courses', component: CourseListComponent },
  { path: 'courses/manage', component: CourseManageComponent },
  { path: 'courses/my-courses', component: RegisteredCoursesComponent }
];

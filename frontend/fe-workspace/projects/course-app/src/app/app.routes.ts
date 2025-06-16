import { Routes } from '@angular/router';
import { DashboardComponent } from './component/dashboard/dashboard.component';
import { AuthGuard, UserProfileComponent } from 'shared-lib';
import { CourseListComponent } from './course/course-list/course-list.component';
import { CourseDetailsComponent } from './course/course-details/course-details.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'profile', component: UserProfileComponent, canActivate: [ AuthGuard ] },
  { path: 'courses', component: CourseListComponent, canActivate: [ AuthGuard ] },
  { path: 'courses/:id', component: CourseDetailsComponent, canActivate: [ AuthGuard ] }
];

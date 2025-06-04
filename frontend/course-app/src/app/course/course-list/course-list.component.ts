import { Component, OnInit } from '@angular/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CourseService } from '../../service/course.service';
import { AuthService } from '../../service/auth.service';
import { Course } from '../../model/course.model';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-course-list',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatSnackBarModule,
    MatCardModule
  ],
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.scss',
  standalone: true
})
export class CourseListComponent implements OnInit {

  courses: Course[] = [];
  isLoading = true;

  constructor(private courseService: CourseService,
                        private authService: AuthService,
                        private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.isLoading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load courses.', 'Dismiss', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  canEdit(): boolean {
    return this.authService.isAdmin();
  }

  canRegister(): boolean {
    return this.authService.isStudent();
  }
}

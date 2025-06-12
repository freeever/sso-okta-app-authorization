import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CourseService } from '../../service/course.service';
import { AuthService } from '../../service/auth.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { Course } from '../../model/course.model';
import { Subject, takeUntil } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/component/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-course-list',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinner
  ],
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.scss',
  standalone: true
})
export class CourseListComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  courses: Course[] = [];
  displayedColumns = ['name', 'description', 'startDate', 'endDate', 'teacher', 'actions'];
  isLoading = true;

  constructor(private courseService: CourseService,
              private authService: AuthService,
              private snackBar: MatSnackBar,
              private router: Router,
              private dialog: MatDialog
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

  viewCourse(id: number) {
    this.router.navigate(['/courses', id]);
  }

  openDeleteDialog(course: Course): void {
    this.dialog.open(ConfirmDialogComponent, {
      width: '600px',
      data: {
        title: 'Confirm Delete',
        contentHtml: `Are you sure you want to delete course <strong>${course.name}</strong>?`,
        confirmButtonLabel: 'Delete',
        cancelButtonLabel: 'Cancel',
        showCancelButton: true,
        onConfirm: () => this.deleteCourse(course.id)
      }
    })
  }

  private deleteCourse(id: number) {
    this.courseService.deleteCourse(id).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: () => {
        this.snackBar.open('✅ Course deleted', 'Dismiss', { duration: 3000 });
        this.loadCourses();
      },
      error: () => {
        this.snackBar.open('❌ Failed to delete course', 'Dismiss', { duration: 3000 });
      }
    });
  }

  addCourse(): void {
    this.router.navigate(['/courses/new']);
  }

  canEdit(): boolean {
    return this.authService.isAdmin();
  }

  canRegister(): boolean {
    return this.authService.isStudent();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

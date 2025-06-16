import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CourseDetails } from '../../model/course-details.model';
import { Subject, takeUntil } from 'rxjs';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../../service/course.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { DateAdapter, MatNativeDateModule, MatOptionModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { AuthService, NotificationService, Role, User, UserAdminService } from 'shared-lib';

@Component({
  selector: 'app-course-details',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatOptionModule,
    MatCardModule
  ],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.scss'
})
export class CourseDetailsComponent implements OnInit, OnDestroy {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private courseService  = inject(CourseService);
  private userService = inject(UserAdminService);
  private authService = inject(AuthService);
  private dateAdapter = inject(DateAdapter<Date>);
  private notificationService = inject(NotificationService);

  private destroy$ = new Subject<void>();

  courseDetails!: CourseDetails;
  allTeachers: User[] = [];
  allStudents: User[] = [];

  courseId?: number;
  isNew: boolean = false;

  form!: FormGroup;
  isEditing = false;

  ngOnInit(): void {
    this.dateAdapter.setLocale('en-CA'); // ensures yyyy-MM-dd format

    this.courseId = +this.route.snapshot.paramMap.get('id')!;
    this.isNew = !this.courseId;
    this.loadCourseDetails();
    this.loadAllUsers();
  }

  loadCourseDetails() {
    if (this.isNew) {
      this.courseDetails = new CourseDetails();
      this.form = this.courseDetails.toForm();
      this.isEditing = true;
    } else {
      this.courseService.getCourseDetails(this.courseId!).pipe(
        takeUntil(this.destroy$)
      ).subscribe(course => {
        this.courseDetails = new CourseDetails(course);
        this.form = this.courseDetails.toForm();
      })
    }
  }

  loadAllUsers(): void {
    this.userService.getAllUsers().pipe(
      takeUntil(this.destroy$)
    ).subscribe(users => {
      this.allTeachers = users.filter(u => u.role === Role.TEACHER.toString());
      this.allStudents = users.filter(u => u.role === Role.STUDENT.toString());
    })
  }

  submit() {
    if (this.form.invalid) return;

    const saveRequest = this.courseDetails.toSaveRequest(this.form);

    const action = this.isNew
      ? this.courseService.createCourse(saveRequest)
      : this.courseService.updateCourse(this.courseId!, saveRequest);

    action.pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (savedCourse) => {
        this.notificationService.success('Course saved successfully');

        // Update the model with new data from response
        this.courseDetails = new CourseDetails(savedCourse);
        // Reset form with updated data
        this.form = this.courseDetails.toForm();

        if (this.isNew) {
          this.gotoList();
        } else {
          // Switch back to view mode
          this.isEditing = false;
        }
      },
      error: () => {
        this.notificationService.error('Failed to save course');
      }
    });
  }

  toggleEdit(): void {
    if (this.isEditing) {
      this.form = this.courseDetails.toForm();
    }
    this.isEditing = !this.isEditing;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  compareUsers(a: User, b: User): boolean {
    return a?.id === b?.id;
  }

  gotoList(): void {
    this.router.navigate(['/courses']);
  }

  cancelEdit(): void {
    this.isEditing = false;
    if (this.isNew) {
      this.router.navigate(['/courses']);
    }
  }

  canDedit() {
    return this.authService.isAdmin();
  }

  get name() {
    return this.form?.get('name');
  }

  get description() {
    return this.form?.get('description');
  }

  get startDate() {
    return this.form?.get('startDate');
  }

  get endDate() {
    return this.form?.get('endDate');
  }

  get teacherId() {
    return this.form?.get('teacherId');
  }

  get enrolledStudentIds() {
    return this.form?.get('enrolledStudentIds');
  }

}

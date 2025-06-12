import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { URL_COURSE, URL_COURSE_MANAGEMENT } from "../shared/core/urls";
import { CourseSaveRequest } from "../model/course-request.model";
import { CourseDetails } from "../model/course-details.model";
import { Course } from "../model/course.model";

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  constructor(private http: HttpClient) {}

  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(URL_COURSE, { withCredentials: true });
  }

  getCourseDetails(id: number): Observable<CourseDetails> {
    return this.http.get<CourseDetails>(`${URL_COURSE}/${id}`, { withCredentials: true });
  }

  updateCourse(id: number, course: CourseSaveRequest): Observable<CourseDetails> {
    return this.http.put<CourseDetails>(`${URL_COURSE_MANAGEMENT}/${id}`, course);
  }

  createCourse(course: CourseSaveRequest): Observable<CourseDetails> {
    return this.http.post<CourseDetails>(URL_COURSE_MANAGEMENT, course);
  }

  deleteCourse(id: number): Observable<any> {
    return this.http.delete<any>(`${URL_COURSE_MANAGEMENT}/${id}`, { withCredentials: true });
  }
}

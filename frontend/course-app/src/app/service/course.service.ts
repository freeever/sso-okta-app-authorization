import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Course } from "../model/course.model";
import { URL_COURSE } from "../shared/core/urls";

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  constructor(private http: HttpClient) {}

  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(URL_COURSE, { withCredentials: true });
  }

  getCourse(id: string): Observable<any> {
    return this.http.get<any>(`${URL_COURSE}/${id}`, { withCredentials: true });
  }

  updateCourse(id: string, course: any): Observable<any> {
    return this.http.put<any>(`${URL_COURSE}/${id}`, course);
  }

  createCourse(course: any): Observable<any> {
    return this.http.post<any>(URL_COURSE, course);
  }

  deleteCourse(id: string): Observable<any> {
    return this.http.delete<any>(`${URL_COURSE}/${id}`, { withCredentials: true });
  }
}

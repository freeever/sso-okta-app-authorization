export interface CourseSaveRequest {
  name: string;
  description?: string;
  startDate?: string;
  endDate?: string;

  teacherId?: number;
  enrolledStudentIds: number[];
}

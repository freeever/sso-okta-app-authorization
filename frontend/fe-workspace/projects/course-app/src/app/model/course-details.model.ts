import { FormControl, FormGroup, Validators } from "@angular/forms";
import { CourseSaveRequest } from "./course-request.model";
import { format } from "date-fns";
import { BaseModel, User } from "shared-lib";

export class CourseDetails extends BaseModel {
  name!: string;
  description!: string;
  startDate!: Date;
  endDate!: Date;

  teacher!: User;
  enrolledStudents: User[] = [];

  teacherId!: number;
  enrolledStudentIds: number[] = [];

  constructor(init? : Partial<CourseDetails>) {
    super();
    Object.assign(this, init);
  }

  toForm(): FormGroup {
    return new FormGroup({
      id: new FormControl(this.id),
      name: new FormControl(this.name, Validators.required),
      description: new FormControl(this.description),
      startDate: new FormControl(this.toDate(this.startDate)),
      endDate: new FormControl(this.toDate(this.endDate)),
      teacherId: new FormControl(this.teacherId),
      enrolledStudentIds: new FormControl(this.enrolledStudentIds)
    })
  }

  toSaveRequest(form: FormGroup): CourseSaveRequest {
    const formValues = form.getRawValue();
    return {
      name: formValues.name,
      description: formValues.description,
      startDate: formValues.startDate ? format(formValues.startDate, 'yyyy-MM-dd') : null,
      endDate: formValues.endDate ? format(formValues.endDate, 'yyyy-MM-dd') : null,

      teacherId: formValues.teacherId,
      enrolledStudentIds: formValues.enrolledStudentIds
    } as CourseSaveRequest;
  }

  toDate(theDate: any) {
    if (!theDate) {
      return null;
    }
    const parts = theDate.split('-');
    return new Date(+parts[0], +parts[1] - 1, +parts[2]);
  }
}

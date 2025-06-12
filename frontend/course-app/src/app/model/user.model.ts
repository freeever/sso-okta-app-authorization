import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Role } from "../shared/core/role.enum";
import { BaseModel } from "./base.model";

export class User extends BaseModel {
  oktaUserId!: string;
  firstName!: string;
  lastName!: string;
  email!: string;
  gender!: string;
  dateOfBirth!: Date;
  role!: string;

  constructor(init? : Partial<User>) {
    super();
    Object.assign(this, init);
    if (!init) {
      this.initDefaults();
    }
  }

  initDefaults() {
    this.role = Role.NONE.toString();
    this.gender = 'OTHER';
    this.dateOfBirth = new Date();
  }

  toForm(): FormGroup {
    return new FormGroup({
      id: new FormControl(this.id),
      oktaUserId: new FormControl(this.oktaUserId, Validators.required),
      firstName: new FormControl(this.firstName, Validators.required),
      lastName: new FormControl(this.lastName, Validators.required),
      email: new FormControl(this.email, Validators.required),
      gender: new FormControl(this.gender, Validators.required),
      dateOfBirth: new FormControl(this.toDate(this.dateOfBirth), Validators.required),
      role: new FormControl(this.role, Validators.required)
    })
  }

  toModel(form: FormGroup): User {
    const formValues = form.getRawValue();
    return new User({
      id: formValues.id,
      oktaUserId: formValues.oktaUserId,
      firstName: formValues.firstName,
      lastName: formValues.lastName,
      email: formValues.email,
      gender: formValues.gender,
      dateOfBirth: formValues.dateOfBirth,
      role: formValues.role,
    })
  }

  toDate(dateOfBirth: any) {
    if (!dateOfBirth) {
      return null;
    }
    const parts = dateOfBirth.split('-');
    return new Date(+parts[0], +parts[1] - 1, +parts[2]);
  }
}

import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Role } from "../shared/core/role.enum";

export class UserForm {
  oktaUserId!: string;
  firstName!: string;
  lastName!: string;
  email!: string;
  gender!: string;
  dateOfBirth!: Date;
  role!: string;

  constructor(init? : Partial<UserForm>) {
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

  toForm() {
    return new FormGroup({
      oktaUserId: new FormControl(this.oktaUserId, Validators.required),
      firstName: new FormControl(this.firstName, Validators.required),
      lastName: new FormControl(this.lastName, Validators.required),
      email: new FormControl(this.email, Validators.required),
      gender: new FormControl(this.gender, Validators.required),
      dateOfBirth: new FormControl(this.dateOfBirth, Validators.required),
      role: new FormControl(this.role, Validators.required)
    })
  }

  toModel(form: FormGroup): UserForm {
    const formValues = form.getRawValue();
    return new UserForm({
      oktaUserId: formValues.oktaUserId,
      firstName: formValues.firstName,
      lastName: formValues.lastName,
      email: formValues.email,
      gender: formValues.gender,
      dateOfBirth: formValues.dateOfBirth,
      role: formValues.role,
    })
  }
}

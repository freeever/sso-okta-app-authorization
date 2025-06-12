import { User } from "./user.model";

export interface Course {
  id: number;
  name: string;
  description: string;
  startDate: Date;
  endDate: Date;
  teacher: User
}

import {Role} from "./role";
import {ShiftType} from "./shiftType";

export interface RangeOption {
  label: string;
  value: string;
}


export interface Day {
  dayName: string;
  date: Date;
}

export interface Shift {
  id: string | null;
  date: string;
  type: ShiftType;
  users: User[];
}

export interface Schedule {
  "id": string | null,
  "month": string,
  "year": number,
  "published": boolean,
  "team": string,
  "shifts": SimpleShift[],
  "monthlyWorkDetails": WorkDetails[]
}

export interface ScheduleWithId {
  id: string,
  published: boolean,
}

export interface WorkDetails {
  userId: string,
  hoursShouldWork : number,
  hoursActuallyWorked: number,
  overtime: number
}

export interface SimpleShift {
  id: string | null;
  date: string | null;
  monthlyPlan: string;
  shiftType: string;
  users: string[];
}

export interface ShiftWithIds {
  id: string | null;
  shiftType: ShiftType;
  date: string | null;
}

export interface EmployeeWithShifts {
  id: string;
  shifts: {
    [date: string]: ShiftWithIds;
  };
}

interface User {
  id: string;
  firstName: string;
  lastName: string;
  workingHoursPercentage: number;
  role: {
    id: string;
    name: string;
  };
}

export interface UserWithShifts {
  id?: string;
  firstName: string;
  lastName: string;
  color: string;
  workingHoursPercentage: number;
  role: Role,
  shifts: ShiftWithIds [];
  workDetails: WorkDetails | null;
}

export interface Employee {
  firstname: string;
  lastname: string;
  working_percentage: number;
  role: string;
}

export interface EmployeeMap {
  name: string;
  role: string;
  workingPercentage: string;
  shifts: {
    [key: string]: Shift;
  };
}

export interface ShiftDetail {
  type: ShiftType;
}

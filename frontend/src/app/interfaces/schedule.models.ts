export interface Schedule {
  month: string;
  year: number;
  days: Day[];
}

export interface Day {
  dayName: string;
  day: number;
  shifts: Shift[];
}

export interface Shift {
  employee: Employee;
  shift: ShiftDetail;
}

export interface Employee {
  firstname: string;
  lastname: string;
  working_percentage: number;
  role: string;
}

export interface ShiftDetail {
  type: ShiftType;
}

export interface ShiftType {
  name: string;
  hexcode: string;
  start_time: string;
  end_time: string;
}

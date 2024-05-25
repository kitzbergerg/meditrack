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
  "shifts": Shift[]
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

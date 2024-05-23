export interface ShiftType {
  id: number;
  name: string;
  startTime: string;
  endTime: string;
  breakStartTime: string;
  breakEndTime: string;
  type: string;
  color: string;
  abbreviation: string;
}

export interface ShiftTypeCreate {
  name: string;
  startTime: string;
  endTime: string;
  breakStartTime: string;
  breakEndTime: string;
  type: string;
  color: string;
  abbreviation: string;
}

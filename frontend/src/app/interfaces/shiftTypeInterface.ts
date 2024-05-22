export interface ShiftType {
  id: number;
  name: string;
  startTime: string;
  endTime: string;
}

export interface ShiftTypeCreate {
  name: string;
  startTime: string;
  endTime: string;
}

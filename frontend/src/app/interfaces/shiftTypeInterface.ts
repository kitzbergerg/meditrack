export interface ShiftType {
  id: number;
  name: string;
  startTime: string;
  endTime: string;
  breakStartTime: string;
  breakEndTime: string;
  type: TypeEnum;
  color: string;
  abbreviation: string;
}

export interface ShiftTypeCreate {
  name: string;
  startTime: string;
  endTime: string;
  breakStartTime: string;
  breakEndTime: string;
  type: TypeEnum;
  color: string;
  abbreviation: string;
}

export enum TypeEnum {
  Default = 'Choose Type',
  Day = 'Day',
  Night = 'Night'
}

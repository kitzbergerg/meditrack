import {Team} from "./team";

export interface ShiftType {
  id?: number;
  name: string;
  startTime: string;
  endTime: string;
  breakStartTime: string;
  breakEndTime: string;
  type: string;
  color: string;
  abbreviation: string;
  team?: Team;
}

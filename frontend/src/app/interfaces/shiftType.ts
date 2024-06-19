import {Team} from "./team";
import {Role} from "./role";

export interface ShiftType {
  id?: number;
  name: string;
  startTime: string;
  endTime: string;
  breakStartTime: string;
  breakEndTime: string;
  color: string;
  abbreviation: string;
  team?: Team;
}

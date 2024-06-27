import {Role} from "./role";
import {ShiftType} from "./shiftType";

export interface User {
  id?: string;
  username?: string; // Nullable in UpdateValidator class
  password: string; // Not nullable in CreateValidator class
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  role: Role ;
  workingHoursPercentage: number;
  currentOverTime?: number | null; // Nullable in CreateValidator class
  team?: string; // Nullable
  holidays: string[]; // List of UUIDs
  preferences?: string; // Nullable
  requestedShiftSwaps: string[]; // List of UUIDs
  suggestedShiftSwaps: string[]; // List of UUIDs
  shifts: string[]; // List of UUIDs
  canWorkShiftTypes: ShiftType[]; // List of ShiftTypes
  preferredShiftTypes: ShiftType[]; // List of ShiftTypes
}


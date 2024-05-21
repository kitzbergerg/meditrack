import {Team} from "./team";
import {Role} from "./roles/rolesInterface";

export interface User {
  id?: string;
  username?: string; // Nullable in UpdateValidator class
  password: string; // Not nullable in CreateValidator class
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];

  role?: Role | null;
  workingHoursPercentage: number;
  currentOverTime?: number | null; // Nullable in CreateValidator class
  specialSkills: string[];
  team?: string; // Nullable
  holidays: string[]; // List of UUIDs
  preferences?: string; // Nullable
  requestedShiftSwaps: string[]; // List of UUIDs
  suggestedShiftSwaps: string[]; // List of UUIDs
  shifts: string[]; // List of UUIDs
  canWorkShiftTypes: string[]; // List of UUIDs
  preferredShiftTypes: string[]; // List of UUIDs
}


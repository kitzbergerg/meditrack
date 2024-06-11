import {Role} from "./role";

export interface Rule {
  name: string;
  label: string;
  value: number | null;
}

export interface RoleRules {
  role: Role;
  daytimeRequiredPeople: number | null,
  nighttimeRequiredPeople: number | null
  allowedFlexitimeTotal: number | null,
  allowedFlexitimeMonthly: number | null,
}

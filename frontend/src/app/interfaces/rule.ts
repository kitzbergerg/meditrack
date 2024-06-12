import {Role} from "./role";

export interface Rule {
  name: string;
  label: string;
  value: number | null;
}

export interface RoleRules {
  role: number; //TODO change name
  daytimeRequiredPeople: number | null,
  nighttimeRequiredPeople: number | null
  allowedFlexitimeTotal: number | null,
  allowedFlexitimeMonthly: number | null,
}

import {Role} from "../roles/rolesInterface";

export interface Rules {
  minRestPeriod: number | null,
  maxShiftLengths: number | null,
  mandatoryOffDays: number | null,
  dayTimeRequiredRoles: [Role | null, number][] | null,
  nightTimeRequiredRoles: [Role | null, number][] | null,
  allowedFlexTimeTotal: number | null,
  allowedFlexTimePerMonth: number | null
}

import {Role} from "../role";
export interface Rules {
  shiftOffShift: [number[], number] | null,
  minRestPeriod: number | null,
  maxShiftLengths: number | null,
  mandatoryOffDays: number | null,
  daytimeRequiredRoles: [Role | null, number][] | null,
  nighttimeRequiredRoles: Map<number, number> | null,
  allowedFlextimeTotal: number | null,
  allowedFlextimePerMonth: number | null
}

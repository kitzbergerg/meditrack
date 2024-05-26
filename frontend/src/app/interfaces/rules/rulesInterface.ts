export interface Rules {
  shiftOffShift: [number[], number] | null,
  minRestPeriod: number | null,
  maximumShiftLengths: number | null,
  mandatoryOffDays: number | null,
  daytimeRequiredRoles: object | null,
  nighttimeRequiredRoles: object | null,
  allowedFlextimeTotal: number | null,
  allowedFlextimePerMonth: number | null
}

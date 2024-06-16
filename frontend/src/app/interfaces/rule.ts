
export interface Rule {
  name: string;
  label: string;
  value: number | null;
}

export interface RoleRules {
  roleId: number;
  daytimeRequiredPeople: number | null,
  nighttimeRequiredPeople: number | null
  allowedFlextimeTotal: number | null,
  allowedFlextimePerMonth: number | null,
}

export interface HardConstraintsDto {
  workingHours: number | null,
  maxWeeklyHours: number | null,
  maxConsecutiveShifts: number | null,
  daytimeRequiredPeople: number | null,
  nighttimeRequiredPeople: number | null,
}

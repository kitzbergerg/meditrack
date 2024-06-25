
export interface Rule {
  name: string;
  label: string;
  description: string;
  value: number | null;
}

export interface RoleRules {
  roleId: number;
  daytimeRequiredPeople: number | null,
  nighttimeRequiredPeople: number | null
  allowedFlextimeTotal: number | null,
  allowedFlextimePerMonth: number | null,
  workingHours: number | null,
  maxWeeklyHours: number | null,
  maxConsecutiveShifts: number | null,
}

export interface HardConstraintsDto {
  daytimeRequiredPeople: number | null,
  nighttimeRequiredPeople: number | null,
}

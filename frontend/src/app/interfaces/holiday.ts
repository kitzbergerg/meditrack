export interface Holiday {
  id?: string;
  startDate: string;
  endDate: string;
  user?: string;
  status?: HolidayRequestStatus
}

export enum HolidayRequestStatus {
  REQUESTED = 'REQUESTED',
  APPROVED = 'APPROVED',
  CANCELLED = 'CANCELLED',
  REJECTED = 'REJECTED'
}

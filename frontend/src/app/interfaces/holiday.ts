export interface Holiday {
  id?: string;
  startDate: string;
  endDate: string;
  user?: string;
  status?: HolidayRequestStatus,
  username?: string;
}

export enum HolidayRequestStatus {
  REQUESTED = 'REQUESTED',
  APPROVED = 'APPROVED',
  CANCELLED = 'CANCELLED',
  REJECTED = 'REJECTED'
}

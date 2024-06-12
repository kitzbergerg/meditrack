import {User} from "./user";
import {ShiftType} from "./shiftType";

export interface ShiftSwap {
  id?: string;
  swapRequestingUser: string,
  requestedShift: ShiftSwapShift,
  requestedShiftSwapStatus: ShiftSwapStatus,
  swapSuggestingUser?: string,
  suggestedShift?: ShiftSwapShift,
  suggestedShiftSwapStatus?: ShiftSwapStatus
}

export interface SimpleShiftSwap {
  id?: string;
  swapRequestingUser: string,
  requestedShift: string,
  requestedShiftSwapStatus: string,
  swapSuggestingUser?: string,
  suggestedShift?: string,
  suggestedShiftSwapStatus?: string
}

export enum ShiftSwapStatus{
  ACCEPTED = 'ACCEPTED',
  PENDING = 'PENDING',
  REJECTED = 'REJECTED'
}

export interface ShiftSwapShift {
  id: string | null;
  date: Date;
  shiftType: ShiftType;
  users: User[];
}

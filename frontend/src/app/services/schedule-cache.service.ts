import { Injectable } from '@angular/core';
import {Schedule, ScheduleWithId, SimpleShift, WorkDetails} from "../interfaces/schedule.models";
import {format} from "date-fns";

@Injectable({
  providedIn: 'root'
})
export class ScheduleCacheService {
  private employeeShiftMap: Map<string, Map<string, SimpleShift>> = new Map<string, Map<string, SimpleShift>>()
  private workDetailsMap: Map<string, Map<string, WorkDetails>> = new Map<string, Map<string, WorkDetails>>();
  private cachedSchedules: { [key: string]: ScheduleWithId } = {};

  getEmployeeShiftMap(): Map<string, Map<string, SimpleShift>> {
    return this.employeeShiftMap;
  }

  setCachedSchedule(cacheKey: string, schedule: ScheduleWithId): void {
    this.cachedSchedules[cacheKey] = schedule;
  }

  setCachedSchedulePublished(cacheKey: string): void {
    this.cachedSchedules[cacheKey].published = true;
  }

  deleteCachedSchedule(cacheKey: string): void {
    delete this.cachedSchedules[cacheKey];
  }

  getCachedSchedule(cacheKey: string): ScheduleWithId | undefined {
    return this.cachedSchedules[cacheKey];
  }

  getWorkDetailsMap(cacheKey: string, employeeId: string): WorkDetails | undefined {
    return this.workDetailsMap.get(employeeId)?.get(cacheKey);
  }

  setWorkDetailsMap(cacheKey: string, workDetails: WorkDetails): void {
    this.workDetailsMap.get(workDetails.userId)?.set(cacheKey, workDetails);
  }

  parseWorkDetailsToMap(workDetails: WorkDetails[], cacheKey: string): void {
    // Maps the work details to the employee work details map
    workDetails.forEach((detail) => {
      if (!this.workDetailsMap.has(detail.userId)) {
        this.workDetailsMap.set(detail.userId, new Map<string, WorkDetails>());
      }
      this.workDetailsMap.get(detail.userId)?.set(cacheKey, detail);
    });
  }

  parseScheduleToMap(schedule: Schedule): void {
    // Maps the schedule to the employee shift map
    schedule.shifts.forEach((shift) => {
      const employeeId = shift.users[0];
      if (!shift.date) {
        return;
      }
      const shiftId = new Date(shift.date); // Create a unique shift identifier

      if (!this.employeeShiftMap.has(employeeId)) {
        this.employeeShiftMap.set(employeeId, new Map<string, SimpleShift>());
      }

      if (this.employeeShiftMap.get(employeeId)) {
        this.employeeShiftMap.get(employeeId)?.set(format(shiftId, 'yyyy-MM-dd'), shift);
      }
    });
  }

  clearEmployeeShiftMap(): void {
    this.employeeShiftMap.clear();
  }
}

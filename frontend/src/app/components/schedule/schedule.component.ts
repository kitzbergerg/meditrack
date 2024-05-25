import {Component, OnInit} from '@angular/core';
import {WeekViewComponent} from "./week-view/week-view.component";
import {Day, EmployeeWithShifts, Shift, SimpleShift} from "../../interfaces/schedule.models";
import {ScheduleService} from "../../services/schedule.service";

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [
    WeekViewComponent
  ],
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.scss'
})
export class ScheduleComponent implements OnInit {

  loading = true;
  days: Day[] = [];
  employees: EmployeeWithShifts[] = [];
  currentWeekOffset = 0;
  startDate: Date = new Date();
  range = 7;
  cachedSchedules: { [key: string]: boolean } = {};

  constructor(private scheduleService: ScheduleService) {
  }

  ngOnInit(): void {
    this.loadSchedule();
  }

  loadSchedule(): void {
    this.startDate = this.getMondayOfCurrentWeek(new Date());
    this.updateData();
  }

  updateData(): void {
    this.generateDays();
    this.getDataIfNotCached();
  }

  fetchMonthSchedule(date: Date): void {
    this.loading = true;
    const month = date.toLocaleString('default', {month: 'long'});
    const year = date.getFullYear();
    console.log("Grabbing data for month: " + month);
    this.scheduleService.createSchedule(month, year).subscribe(data => {
      this.transformData(data.shifts);
    });
  }

  generateDays(): void {
    const days = [];
    const date = new Date(this.startDate);
    for (let i = 0; i < this.range; i++) {
      const dayName = this.getDayString(date);
      days.push({dayName, date});
      date.setDate(date.getDate() + 1);
    }
    this.days = days;
  }

  getDataIfNotCached(): void {
    const currentDate = new Date(this.startDate);
    const endDate = new Date(this.startDate);
    endDate.setDate(this.startDate.getDate() + (this.range-1));
    console.log("Checking start day: " + currentDate)
    console.log("Checking end day: " + endDate)
    // Check if month of first and last day has been fetched
    let cacheKey = this.generateCacheKey(currentDate);
    if (!this.cachedSchedules[cacheKey]) {
      this.fetchMonthSchedule(currentDate);
      this.cachedSchedules[cacheKey] = true;
    }

    cacheKey = this.generateCacheKey(endDate);
    if (!this.cachedSchedules[cacheKey]) {
      this.fetchMonthSchedule(endDate);
      this.cachedSchedules[cacheKey] = true;
    }
  }

  transformData(shifts: Shift[]): void {
    const employeeMap = new Map<string, EmployeeWithShifts>();

    if (this.employees) {
      this.employees.forEach(employee => {
        employeeMap.set(employee.name, employee);
      });
    }

    shifts.forEach(shift => {
      const date = new Date(shift.date).toDateString();
      shift.users.forEach(user => {
        const name = `${user.firstName} ${user.lastName}`;
        const role = user.role.name;
        const workingPercentage = `${user.workingHoursPercentage}`;

        let employee = employeeMap.get(name);
        if (!employee) {
          employee = {
            name,
            role,
            workingPercentage,
            shifts: {}
          };
          employeeMap.set(name, employee);
        }
        employee.shifts[date] = {
          id: shift.id,
          date: shift.date,
          type: shift.type,
        };
      });
    });

    this.employees = Array.from(employeeMap.values());
    this.loading = false;
  }

  getMondayOfCurrentWeek(date: Date): Date {
    const today = date;
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is Sunday
    return new Date(today.setDate(diff));
  }

  generateCacheKey(currentDate: Date){
    const month = currentDate.toLocaleString('default', {month: 'long'});
    const year = currentDate.getFullYear();
    return `${month}-${year}`;
  }

  getDayString(date: Date): string {
    const dayString = date.toLocaleDateString('en-GB', {day: '2-digit', month: '2-digit'});
    const dayOfWeekString = date.toLocaleDateString('en-GB', {weekday: 'short'});
    return `${dayString} ${dayOfWeekString}`;
  }

  changeWeek(offset: number): void {
    this.currentWeekOffset += offset;
    this.startDate.setDate(this.startDate.getDate() + (offset * this.range));
    this.updateData()
  }

  changeRange(range: number): void {
    this.range = range;
    this.updateData();
  }

}

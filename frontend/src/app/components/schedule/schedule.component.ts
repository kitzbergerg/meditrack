import {Component, OnInit} from '@angular/core';
import {WeekViewComponent} from "./week-view/week-view.component";
import {Day, Shift} from "../../interfaces/schedule.models";
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

  allDays: Day[] = [];
  allShifts: Shift[] = [];
  days: Day[] = [];
  employees: any[] = [];
  currentWeekOffset = 0;
  startDate: Date = new Date();
  range = 7;

  constructor(private scheduleService: ScheduleService) {
  }

  ngOnInit(): void {
    this.loadSchedule();
  }

  loadSchedule(): void {
    this.scheduleService.createSchedule().subscribe(data => {
      this.allDays = this.transformDays(data.shifts);
      this.allShifts = data.shifts;
      this.startDate = new Date(data.year, this.getMonthFromString(data.month), 1);
      this.loadWeek();
    });
  }

  transformDays(shifts: Shift[]): Day[] {
    const dayMap = new Map<number, Day>();

    shifts.forEach(shift => {
      const date = new Date(shift.date);
      const day = date.getDate();
      const dayName = this.getDayString(day - (this.currentWeekOffset * this.range));

      if (!dayMap.has(day)) {
        dayMap.set(day, {dayName, day, shifts: []});
      }
      dayMap.get(day)?.shifts.push(shift);
    });

    return Array.from(dayMap.values()).sort((a, b) => a.day - b.day);
  }

  loadWeek(): void {
    this.days = this.allDays.slice(0, this.range);
    const start = this.currentWeekOffset * this.range;
    const end = start + this.range;
    const filteredShifts = this.allShifts.filter(shift => {
      const shiftDate = new Date(shift.date).getDate();
      return shiftDate >= start && shiftDate < end;
    });
    this.transformData(filteredShifts);
  }


  transformData(shifts: Shift[]): void {
    const employeeMap = new Map<string, any>();

    shifts.forEach(shift => {
      const day = new Date(shift.date).getDate();
      const dayName = this.getDayString(day - (this.currentWeekOffset * this.range));

      shift.users.forEach(user => {
        const name = `${user.firstName} ${user.lastName}`;
        const role = user.role.name;
        const workingPercentage = `${user.workingHoursPercentage}`;
        if (!employeeMap.has(name)) {
          employeeMap.set(name, {name, role, workingPercentage, shifts: {}});
        }
        employeeMap.get(name).shifts[day] = {...shift, dayName};
      });
    });

    this.employees = Array.from(employeeMap.values());

  }

  getMonthFromString(month: string): number {
    const monthNames = ["JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"];
    return monthNames.indexOf(month.toUpperCase());
  }

  getDayString(dayIndex: number): string {
    const date = new Date(this.startDate);
    date.setDate(this.startDate.getDate() + dayIndex - 1);
    const dayString = date.toLocaleDateString('en-GB', {day: '2-digit', month: '2-digit'});
    const dayOfWeekString = date.toLocaleDateString('en-GB', {weekday: 'short'});
    return `${dayString} ${dayOfWeekString}`;
  }

  changeWeek(offset: number): void {
    this.currentWeekOffset += offset;
    this.startDate.setDate(this.startDate.getDate() + (offset * this.range));
    this.loadWeek();
  }

  changeRange(range: number): void {
    this.range = range;
    this.loadWeek();
  }

}

import {Component, OnInit} from '@angular/core';
import {WeekViewComponent} from "./week-view/week-view.component";
import {Day} from "../../interfaces/schedule.models";
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
    this.scheduleService.getSchedule().subscribe(data => {
      this.allDays = data.days;
      this.startDate = new Date(data.year, data.month, 1);
      this.loadWeek();
    });
  }

  loadWeek(): void {
    const start = this.currentWeekOffset * this.range;
    this.days = this.allDays.slice(start, start + this.range);
    this.transformData(this.days);
  }

  transformData(days: Day[]): void {
    const employeeMap = new Map<string, any>();

    days.forEach(day => {
      day.dayName = this.getDayString(day.day - (this.currentWeekOffset * this.range));
      day.shifts.forEach(shift => {
        const name = `${shift.employee.firstname} ${shift.employee.lastname}`;
        const role = shift.employee.role;
        const workingPercentage = `${shift.employee.working_percentage * 100}`;
        if (!employeeMap.has(name)) {
          employeeMap.set(name, {name, role, workingPercentage, shifts: {}});
        }
        employeeMap.get(name).shifts[day.day] = shift;
      });
    });

    this.employees = Array.from(employeeMap.values());
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

import {Component, OnInit} from '@angular/core';
import {DayColumnComponent} from "../day-column/day-column.component";
import {ScheduleService} from "../../../services/schedule.service";
import {Day} from "../../../interfaces/schedule.models";
import {NgForOf, NgIf, NgStyle} from "@angular/common";
import {TableModule} from "primeng/table";

@Component({
  selector: 'app-week-view',
  standalone: true,
  imports: [
    DayColumnComponent,
    NgForOf,
    TableModule,
    NgStyle,
    NgIf
  ],
  templateUrl: './week-view.component.html',
  styleUrl: './week-view.component.scss'
})
export class WeekViewComponent implements OnInit{

  days: string[] = ['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'];
  employees: any[] = [];
  data: Day[] | undefined;

  constructor(private scheduleService: ScheduleService) { }

  ngOnInit(): void {
    this.scheduleService.getSchedule().subscribe(data => {
      this.data = data;
      this.transformData(data.days);
      console.log(data)
    });
  }

  transformData(days: Day[]): void {
    const employeeMap = new Map<string, any>();

    days.forEach(day => {
      day.shifts.forEach(shift => {
        const name = `${shift.employee.firstname} ${shift.employee.lastname}`;
        const workingPercentage = `${shift.employee.working_percentage * 100}`;
        const role = shift.employee.role;
        if (!employeeMap.has(name)) {
          employeeMap.set(name, { name, role, workingPercentage, shifts: {} });
        }
        employeeMap.get(name).shifts[day.day] = shift;
      });
    });

    this.employees = Array.from(employeeMap.values());
    console.log(this.employees);

  }

  getFormattedDateRange(): string {
    if (this.startDate) {
      const endDate = new Date(this.startDate);
      endDate.setDate(this.startDate.getDate() + 6);
      return `${this.startDate.toLocaleDateString('en-GB')} - ${endDate.toLocaleDateString('en-GB')}`;
    }
    return "";
  }

  previousWeek(): void {
    this.weekChange.emit(-1);
  }

  nextWeek(): void {
    this.weekChange.emit(1);
  }

}

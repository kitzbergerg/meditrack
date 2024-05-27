import {Component, OnInit} from '@angular/core';
import {WeekViewComponent} from "./week-view/week-view.component";
import {Day, EmployeeWithShifts, Shift} from "../../interfaces/schedule.models";
import {ScheduleService} from "../../services/schedule.service";
import {RolesService} from "../../services/roles.service";
import {Role} from "../../interfaces/role";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";

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
  employees: Map<string, EmployeeWithShifts> = new Map<string, EmployeeWithShifts>();
  currentWeekOffset = 0;
  startDate: Date = new Date();
  range = 7;
  cachedSchedules: { [key: string]: Shift[] } = {};
  displayCreateScheduleButton = false;
  createScheduleMonth = "";
  roles: Role[] = [];
  users: User[] = [];
  constructor(private scheduleService: ScheduleService, private roleService: RolesService, private userService: UserService) {
  }

  ngOnInit(): void {
    this.fetchRoles();
    this.loadSchedule();
  }

  loadSchedule(): void {
    this.startDate = this.getMondayOfCurrentWeek(new Date());
    this.startDate.setHours(0, 0, 0, 0);
    this.updateData();
  }

  updateData(): void {
    this.generateDays();
    this.getUsersFromTeam();
    this.getDataIfNotCached();
  }

  createSchedule(): void {
    this.loading = true;
    const year = this.startDate.getFullYear();
    this.scheduleService.createSchedule(this.createScheduleMonth, year).subscribe(() => {
      this.updateData();
      this.displayCreateScheduleButton = false;
    });
  }

  fetchMonthSchedule(date: Date): void {
    this.loading = true;
    const month = date.toLocaleString('default', {month: 'long'});
    const year = date.getFullYear();
    console.log("Grabbing data for month: " + month);
    this.scheduleService.fetchSchedule(month, year).subscribe({
      next: data => {
        const cacheKey = this.generateCacheKey(date);
        this.cachedSchedules[cacheKey] = data.shifts;
        this.transformData(data.shifts);
      },
      error: err => {
        if (err.status === 404) {
          this.displayCreateScheduleButton = true;
          this.createScheduleMonth = month;
          this.loading = false;
        }
      }
    });
  }

  generateDays(): void {
    const days = [];
    let iterateDate = new Date(this.startDate);
    console.log("start date: " + iterateDate);
    for (let i = 0; i < this.range; i++) {
      const dayName = this.getDayString(iterateDate);
      const date = new Date(iterateDate);
      days.push({dayName, date});
      iterateDate = new Date(iterateDate.setDate(iterateDate.getDate() + 1));
    }
    this.days = days;
  }

  getDataIfNotCached(): void {
    const currentDate = new Date(this.startDate);
    const endDate = new Date(this.startDate);
    endDate.setDate(this.startDate.getDate() + (this.range - 1));
    console.log("Checking start day: " + currentDate)
    console.log("Checking end day: " + endDate)

    // Delete old data
    this.employees = new Map<string, EmployeeWithShifts>();

    // Fill this.employees with new data
    // Check if month data of first Day and last Day are cached
    this.checkAndFetchSchedule(currentDate);
    this.checkAndFetchSchedule(endDate);
  }

  checkAndFetchSchedule(date: Date): void {
    const cacheKey = this.generateCacheKey(date);
    if (!this.cachedSchedules[cacheKey]) {
      this.fetchMonthSchedule(date);
    } else {
      this.transformData(this.cachedSchedules[cacheKey]);
    }
  }

  transformData(shifts: Shift[]): void {
    const employeeMap = new Map<string, EmployeeWithShifts>();

    const endDate = new Date(this.startDate);
    endDate.setDate(this.startDate.getDate() + this.range);

    // Filter shifts based on the date range
    const filteredShifts = shifts.filter(shift => {
      const shiftDate = new Date(shift.date);
      return shiftDate >= this.startDate && shiftDate < endDate;
    });

    // Fill map with employees, so we do not overwrite them in second transformData call in getDataIfNotCached()
    this.employees.forEach(emp => {
      const name = emp.name;
      const role = emp.role;
      const workingPercentage = emp.workingPercentage;

      if (!employeeMap.has(name)) {
        employeeMap.set(name, {
          name,
          role,
          workingPercentage,
          shifts: emp.shifts
        });
      }
    });

    // Iterate over shifts and parse the data, store in employeeMap
    filteredShifts.forEach(shift => {
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

    this.employees = employeeMap;
    this.loading = false;
  }

  getMondayOfCurrentWeek(date: Date): Date {
    const today = date;
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is Sunday
    return new Date(today.setDate(diff));
  }

  generateCacheKey(currentDate: Date) {
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
    if (this.range > 14) {
      this.startDate = new Date(this.startDate.getFullYear(), this.startDate.getMonth() + offset, 1);
      this.range = new Date(this.startDate.getFullYear(), this.startDate.getMonth() + offset, 0).getDate();
    } else {
      this.startDate.setDate(this.startDate.getDate() + (offset * this.range));
    }
    this.startDate.setHours(0, 0, 0, 0);
    this.updateData()
  }

  changeRange(range: string): void {
    switch (range) {
      case 'week':
        this.startDate = this.getMondayOfCurrentWeek(this.startDate);
        this.range = 7;
        break;
      case '2weeks':
        this.startDate = this.getMondayOfCurrentWeek(this.startDate);
        this.range = 14;
        break;
      case 'month':
        this.startDate = new Date(this.startDate.getFullYear(), this.startDate.getMonth(), 1);
        this.range = new Date(this.startDate.getFullYear(), this.startDate.getMonth() + 1, 0).getDate();
        break;
      default:
        throw new Error(`Unknown range: ${range}`);
    }
    this.updateData();
  }

  fetchRoles(): void {
    this.roleService.getAllRolesFromTeam().subscribe({
      next: data => {
        this.roles = data;
      }
    });
  }

  getUsersFromTeam(): void {
    this.userService.getAllUserFromTeam().subscribe({
      next: data => {
        this.users = data;
      }
    });
  }

}

import {Component, OnInit} from '@angular/core';
import {WeekViewComponent} from "./week-view/week-view.component";
import {Day, EmployeeWithShifts, Schedule, Shift, SimpleShift} from "../../interfaces/schedule.models";
import {ScheduleService} from "../../services/schedule.service";
import {RolesService} from "../../services/roles.service";
import {Role} from "../../interfaces/role";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";
import {ShiftTypeService} from "../../services/shiftType.service";
import {ShiftType} from "../../interfaces/shiftType";
import {ShiftService} from "../../services/shift.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";

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
  cachedSchedules: { [key: string]: Schedule } = {};
  displayCreateScheduleButton = false;
  createScheduleMonth = "";
  roles: Role[] = [];
  users: User[] = [];
  shiftTypes: ShiftType[] = [];
  currentUser: User | undefined;

  constructor(private scheduleService: ScheduleService, private roleService: RolesService,
              private userService: UserService, private shiftTypeService: ShiftTypeService,
              private shiftService: ShiftService, private authorizationService: AuthorizationService) {
  }

  ngOnInit(): void {
    this.fetchRoles();
    this.loadSchedule();
    this.loadShiftTypes();
    this.getCurrentUser();
  }

  loadSchedule(): void {
    this.startDate = this.getMondayOfCurrentWeek(new Date());
    this.startDate.setHours(0, 0, 0, 0);
    this.getUsersFromTeam();
    this.updateData();
  }

  updateData(): void {
    this.generateDays();
    this.getDataIfNotCached();
  }

  createSchedule(): void {
    this.loading = true;
    const year = this.startDate.getFullYear();
    this.scheduleService.createSchedule(this.createScheduleMonth, year).subscribe(() => {
      this.updateData();
      const month = new Date(`${this.createScheduleMonth} 1, ${year}`).getMonth() + 1;
      this.startDate = new Date(this.startDate.getFullYear(), month, 0);
      this.changeRange("month");
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
        this.cachedSchedules[cacheKey] = data;
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
      this.transformData(this.cachedSchedules[cacheKey].shifts);
    }
  }

  transformData(shifts: SimpleShift[]): void {
    const employeeMap = new Map<string, EmployeeWithShifts>();

    const endDate = new Date(this.startDate);
    endDate.setDate(this.startDate.getDate() + this.range);

    // Filter shifts based on the date range
    const filteredShifts = shifts.filter(shift => {
      if (!shift.date) {
        return;
      }
      const shiftDate = new Date(shift.date);
      return shiftDate >= this.startDate && shiftDate < endDate;
    });

    // Fill map with employees, so we do not overwrite them in second transformData call in getDataIfNotCached()
    this.employees.forEach(emp => {
      const id = emp.id;

      if (!employeeMap.has(id)) {
        employeeMap.set(id, {
          id: id,
          shifts: emp.shifts
        });
      }
    });

    // Iterate over shifts and parse the data, store in employeeMap
    filteredShifts.forEach(shift => {
      if (!shift.date) {
        return;
      }
      const date = new Date(shift.date).toDateString();
      shift.users.forEach(user => {
        const id = `${user}`;

        let employee = employeeMap.get(id);
        if (!employee) {
          employee = {
            id: id,
            shifts: {}
          };
          employeeMap.set(id, employee);
        }
        employee.shifts[date] = {
          id: shift.id,
          date: shift.date,
          shiftType: shift.shiftType,
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
    this.displayCreateScheduleButton = false;
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
    this.displayCreateScheduleButton = false;
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

  loadShiftTypes() {
    this.shiftTypeService.getAllShiftTypesByTeam().subscribe({
      next: (response) => {
        this.shiftTypes = response;
      }
    });
  }

  updateShift(shiftInfo: {
    user: User,
    day: Day,
    shiftType: ShiftType,
    shiftId: string | null,
    operation: string
  }): void {
    let shiftDate = new Date(shiftInfo.day.date.toDateString());
    const cacheKey = this.generateCacheKey(shiftDate);
    const scheduleId = this.cachedSchedules[cacheKey].id;
    if (shiftInfo.user.id == undefined || scheduleId == undefined || shiftInfo.shiftType.id == undefined) {
      return;
    }
    // Calculation so different timezones do not lead to wrong dates
    const offset = shiftDate.getTimezoneOffset()
    shiftDate = new Date(shiftDate.getTime() - (offset * 60 * 1000))
    const shiftDateString = shiftDate.toISOString().split('T')[0]

    const shift: SimpleShift = {
      id: shiftInfo.shiftId,
      date: shiftDateString,
      monthlyPlan: scheduleId,
      shiftType: shiftInfo.shiftType.id.toString(),
      users: [
        shiftInfo.user.id
      ],
    };

    switch (shiftInfo.operation.toLowerCase()) {
      case 'create':
        this.shiftService.createShift(shift).subscribe({
          next: (response) => {
            this.cachedSchedules[cacheKey].shifts.push(response);
            this.updateData();
          }
        });
        break;
      case 'delete':
        if (shiftInfo.shiftId == null) {
          return;
        }
        this.shiftService.deleteShift(shiftInfo.shiftId).subscribe({
          next: () => {
            this.cachedSchedules[cacheKey].shifts = this.cachedSchedules[cacheKey].shifts.filter(
              s => s.id !== shiftInfo.shiftId
            );
            this.updateData();
          }
        })
        break;
      case 'update':
        if (shiftInfo.shiftId == null) {
          return;
        }
        this.shiftService.updateShift(shift).subscribe({
          next: (response) => {
            this.cachedSchedules[cacheKey].shifts = this.cachedSchedules[cacheKey].shifts.filter(
              s => s.id !== response.id
            );
            this.cachedSchedules[cacheKey].shifts.push(response);
            this.updateData();
          }
        })
        break;
    }
  }

  deleteSchedule(): void {
    this.loading = true;
    const shiftDate = new Date(this.startDate);
    const cacheKey = this.generateCacheKey(shiftDate);
    const scheduleId = this.cachedSchedules[cacheKey].id;
    // TODO: add error msg
    if (scheduleId == null) {
      return;
    }
    this.scheduleService.deleteSchedule(scheduleId).subscribe(() => {
      delete this.cachedSchedules[cacheKey];
      this.updateData();
      this.displayCreateScheduleButton = true;
    });
  }

  getCurrentUser(): void {
    const userId = this.authorizationService.parsedToken().sub;
    this.userService.getUserById(userId).subscribe({
      next:
        (response) => {
          this.currentUser = response;
        }
    });
  }

}

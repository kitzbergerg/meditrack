import {Component, OnInit} from '@angular/core';
import {WeekViewComponent} from "./week-view/week-view.component";
import {
  Day,
  EmployeeWithShifts,
  Schedule, ScheduleWithId,
  SimpleShift,
  UserWithShifts,
  WorkDetails
} from "../../interfaces/schedule.models";
import {ScheduleService} from "../../services/schedule.service";
import {RolesService} from "../../services/roles.service";
import {Role} from "../../interfaces/role";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";
import {ShiftTypeService} from "../../services/shift-type.service";
import {ShiftService} from "../../services/shift.service";
import {ShiftType} from "../../interfaces/shiftType";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {MessageService} from "primeng/api";
import {ToastModule} from "primeng/toast";
import {
  startOfWeek,
  addDays,
  format,
  parseISO,
  addMonths,
  subMonths,
  endOfMonth,
  setHours,
  getDate,
  getYear,
  startOfMonth,
  getISOWeek
} from 'date-fns';
import {ScheduleLegendComponent} from "./schedule-legend/schedule-legend.component";

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [
    WeekViewComponent,
    ToastModule,
    ScheduleLegendComponent
  ],
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.scss'
})
export class ScheduleComponent implements OnInit {

  loading = true;
  days: Day[] = [];
  employees: Map<string, EmployeeWithShifts> = new Map<string, EmployeeWithShifts>();
  employeeShiftMap: Map<string, Map<string, SimpleShift>> = new Map<string, Map<string, SimpleShift>>();
  workDetailsMap: Map<string, Map<string, WorkDetails>> = new Map<string, Map<string, WorkDetails>>();
  currentWeekOffset = 0;
  startDate: Date = new Date();
  range = 7;
  cachedSchedules: { [key: string]: ScheduleWithId } = {};
  currentSchedule: ScheduleWithId | undefined;
  displayCreateScheduleButton = false;
  createScheduleMonth = "";
  roles: Role[] = [];
  users: User[] = [];
  usersWithShifts: UserWithShifts[] = [];
  shiftTypes: { [id: string]: ShiftType } = {};
  currentUser: User | undefined;
  userWorkDetails: WorkDetails[] = [];
  currentPlanId: string | null = null;
  currentPlanPublished = false;
  weekNumber: number | undefined;
  monthString: string | undefined;

  constructor(private scheduleService: ScheduleService, private roleService: RolesService,
              private userService: UserService, private shiftTypeService: ShiftTypeService,
              private authorizationService: AuthorizationService, private shiftService: ShiftService,
              private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.fetchRoles();
    this.loadShiftTypes();
    this.getCurrentUser();
    this.loadSchedule();
  }

  loadSchedule(): void {
    this.startDate = setHours(this.getMondayOfCurrentWeek(new Date()), 12);
    this.getUsersFromTeam();
    this.updateData();
  }

  updateData(): void {
    this.loading = true;
    this.setMonthName(this.startDate);
    this.setWeekNumber(this.startDate);
    this.generateDays();
    this.getDataIfNotCached().then();
  }

  createSchedule(): void {
    this.loading = true;
    const year = this.startDate.getFullYear();
    this.scheduleService.createSchedule(this.createScheduleMonth, year).subscribe((data) => {
      const month = new Date(`${this.createScheduleMonth} 1, ${year}`).getMonth() + 1;
      this.startDate = new Date(this.startDate.getFullYear(), month, 0);
      const cacheKey = this.generateCacheKey(this.startDate);
      if (!this.cachedSchedules[cacheKey] && data.id != null) {
        this.cachedSchedules[cacheKey] = {id: data.id, published: data.published};
      }
      this.parseScheduleToMap(data);
      this.parseWorkDetailsToMap(data.monthlyWorkDetails, cacheKey);
      this.changeRange("month");
      this.displayCreateScheduleButton = false;
      this.setCurrentSchedule();
      this.updateData();
    });
  }

  async fetchMonthSchedule(date: Date): Promise<void> {
    const month = format(date, 'MMMM');
    const year = getYear(date);
    return new Promise((resolve, reject) => {
      this.scheduleService.fetchSchedule(month, year).subscribe({
        next: data => {
          const cacheKey = this.generateCacheKey(date);
          if (data.id == null) {
            resolve();
            return;
          }
          if (!this.cachedSchedules[cacheKey]) {
            this.cachedSchedules[cacheKey] = {id: data.id, published: data.published};
            this.parseScheduleToMap(data);
            this.parseWorkDetailsToMap(data.monthlyWorkDetails, cacheKey);
          }

          this.userWorkDetails = data.monthlyWorkDetails;
          this.displayCreateScheduleButton = false;
          this.setCurrentSchedule();
          resolve();
        },
        error: err => {
          if (err.status === 404) {
            this.displayCreateScheduleButton = true;
            this.createScheduleMonth = month;
            this.usersWithShifts.forEach(user => {
              user.shifts = Array(this.days.length).fill(null);
            });
            this.loading = false;
            resolve();
          }
          if (err.status === 403) {
            this.displayCreateScheduleButton = false;
            if (this.range > 14) {
              this.messageService.add({severity: 'info', summary: 'Schedule not published yet!'});
            }
            this.loading = false;
            resolve();
          }
          reject(err);
        }
      });
    });
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

  deleteMonthlyPlanFromMap(monthlyPlan: string): void {
    // Iterate through each employee's shift map
    this.employeeShiftMap.forEach((shiftMap, employeeId) => {
      // Create a list to store keys (shift IDs) to be deleted
      const keysToDelete: string[] = [];

      // Iterate through each shift for the employee
      shiftMap.forEach((shift, shiftId) => {
        // Check if the shift's monthlyPlan matches the specified monthly plan
        if (shift.monthlyPlan === monthlyPlan) {
          keysToDelete.push(shiftId);
        }
      });

      // Delete the shifts with the specified monthly plan
      keysToDelete.forEach((key) => shiftMap.delete(key));
    });
  }

  setCurrentSchedule(): void {
    const cacheKey = this.generateCacheKey(this.startDate);
    this.currentSchedule = this.cachedSchedules[cacheKey];
  }

  mapUsers(users: User[]): UserWithShifts[] {
    return users.map(user => ({
      id: user.id, firstName: user.firstName, lastName: user.lastName,
      workingHoursPercentage: user.workingHoursPercentage, role: user.role, color:
        this.getColorFromName(user.firstName + user.lastName), shifts: [], workDetails: null
    }));
  }

  generateDays(): void {
    const days = [];
    let iterateDate = this.startDate;
    for (let i = 0; i < this.range; i++) {
      const dayName = this.getDayString(iterateDate);
      const date = iterateDate;
      days.push({dayName, date});
      iterateDate = addDays(iterateDate, 1);
    }
    this.days = days;
  }

  async getDataIfNotCached(): Promise<void> {
    const currentDate = parseISO(format(this.startDate, 'yyyy-MM-dd'));
    const endDate = addDays(currentDate, this.range - 1);

    // Check if month data of first day and last day are already fetched
    await this.checkAndFetchSchedule(currentDate);
    if (currentDate.getMonth() !== endDate.getMonth()) {
      await this.checkAndFetchSchedule(endDate);
    }

    this.setCurrentSchedule();
    this.transformData(endDate);
  }

  async checkAndFetchSchedule(date: Date): Promise<void> {
    const cacheKey = this.generateCacheKey(date);
    if (!this.cachedSchedules[cacheKey]) {
      await this.fetchMonthSchedule(date);
    }
  }

  transformData(calendarDate: Date): void {
    this.loading = true;
    // Iterate over shifts and parse the data, store in the respective employee's shifts map
    this.usersWithShifts.forEach(employee => {
      if (!employee.id) {
        return;
      }
      // Initialize the shifts array with null or empty objects to match the length of this.days
      employee.shifts = Array(this.days.length).fill(null);
      const details = this.workDetailsMap.get(employee.id)?.get(this.generateCacheKey(calendarDate));
      if (details) {
        employee.workDetails = details;
      } else {
        employee.workDetails = null;
      }

      // Iterate over all days to populate the shifts array
      this.days.forEach((day, index) => {
        // Get the shift for the current day from the cached data
        if (!employee.id) {
          return;
        }
        const date = parseISO(format(day.date, 'yyyy-MM-dd'));
        const shift = this.employeeShiftMap.get(employee.id) && this.employeeShiftMap.get(employee.id)?.get(format(date, 'yyyy-MM-dd'));
        if (shift) {
          const shiftType = this.shiftTypes[shift.shiftType];
          employee.shifts[index] = {
            id: shift.id,
            date: shift.date,
            shiftType: shiftType,
          };
        }
      });
    });

    this.loading = false;
  }

  getMondayOfCurrentWeek(date: Date): Date {
    return startOfWeek(date, {weekStartsOn: 1});
  }

  setWeekNumber(date: Date): void {
    const formattedDate = parseISO(format(date, 'yyyy-MM-dd'));
    this.weekNumber = getISOWeek(formattedDate);
  }

  setMonthName(date: Date): void {
    this.monthString = format(date, 'MMMM'); // Formats the date to return the full month name
  }

  generateCacheKey(currentDate: Date): string {
    const month = format(currentDate, 'MMMM');
    const year = getYear(currentDate);
    return `${month}-${year}`;
  }

  getDayString(date: Date): string {
    const dayString = format(date, 'dd/MM');
    const dayOfWeekString = format(date, 'EEE');
    return `${dayString} ${dayOfWeekString}`;
  }

  changeWeek(offset: number): void {
    this.displayCreateScheduleButton = false;
    this.currentWeekOffset += offset;
    if (this.range > 14) {
      this.startDate = offset > 0 ? addMonths(this.startDate, offset) : subMonths(this.startDate, -offset);
      this.range = getDate(endOfMonth(this.startDate));
    } else {
      this.startDate = addDays(this.startDate, offset * this.range);
    }
    this.startDate = setHours(this.startDate, 12);
    this.updateData()
  }

  changeRange(range: string): void {
    this.displayCreateScheduleButton = false;
    switch (range) {
      case 'week':
        this.startDate = this.startDate = setHours(this.getMondayOfCurrentWeek(new Date()), 12);
        this.range = 7;
        break;
      case '2weeks':
        this.startDate = this.startDate = setHours(this.getMondayOfCurrentWeek(new Date()), 12);
        this.range = 14;
        break;
      case 'month':
        this.startDate = startOfMonth(this.startDate);
        this.range = getDate(endOfMonth(this.startDate));
        this.currentPlanPublished = this.cachedSchedules[this.generateCacheKey(this.startDate)].published;
        this.currentPlanId = this.cachedSchedules[this.generateCacheKey(this.startDate)].id;
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
        this.usersWithShifts = this.mapUsers(data);
      }
    });
  }

  loadShiftTypes() {
    this.shiftTypeService.getAllShiftTypesByTeam().subscribe({
      next: (response) => {
        this.shiftTypes = {};

        for (const shiftType of response) {
          if (shiftType.id != undefined) {
            this.shiftTypes[shiftType.id] = shiftType;
          }
        }
      }
    });
  }

  updateShift(shiftInfo: {
    user: UserWithShifts,
    day: Day,
    shiftType: ShiftType,
    shiftId: string | null,
    operation: string
  }): void {
    const shiftDate = parseISO(format(shiftInfo.day.date, 'yyyy-MM-dd'));
    const shiftDateString = format(shiftDate, 'yyyy-MM-dd');

    const cacheKey = this.generateCacheKey(shiftDate);
    const scheduleId = this.cachedSchedules[cacheKey]?.id;
    if (shiftInfo.user.id == undefined || scheduleId == undefined || shiftInfo.shiftType.id == undefined) {
      return;
    }
    // Calculation so different timezones do not lead to wrong dates

    const shift: SimpleShift = {
      id: shiftInfo.shiftId,
      date: shiftDateString,
      monthlyPlan: scheduleId,
      shiftType: shiftInfo.shiftType.id.toString(),
      users: [
        shiftInfo.user.id
      ],
    };

    const curEmployee = this.usersWithShifts.find(user => user.id === shiftInfo.user.id);

    switch (shiftInfo.operation.toLowerCase()) {
      case 'create':
        this.shiftService.createShift(shift).subscribe({
          next: (response) => {
            this.messageService.add({severity: 'success', summary: 'Successfully added shift'});
            shift.id = response.id;
            this.employeeShiftMap.get(response.users[0])?.set(shiftDateString, shift);
            if (shiftInfo.user.id != undefined) {
              this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
            }
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Creating shift failed: ' + error.error});
          }
        });
        break;
      case 'delete':
        if (shiftInfo.shiftId == null) {
          return;
        }
        this.shiftService.deleteShift(shiftInfo.shiftId).subscribe({
          next: () => {
            this.messageService.add({severity: 'success', summary: 'Successfully deleted shift'});
            if (curEmployee?.id) {
              this.employeeShiftMap.get(curEmployee.id)?.delete(shiftDateString);
            }
            if (shiftInfo.user.id != undefined) this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Deleting shift failed: ' + error.error});
          }
        })
        break;
      case 'update':
        if (shiftInfo.shiftId == null) {
          return;
        }
        this.shiftService.updateShift(shift).subscribe({
          next: (data) => {
            this.messageService.add({severity: 'success', summary: 'Successfully updated shift'});
            this.employeeShiftMap.get(data.users[0])?.set(shiftDateString, shift);
            if (shiftInfo.user.id != undefined) this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Updating shift failed: ' + error.error});
          }
        })
        break;
    }
  }

  fetchWorkDetails(userId: string, shiftDate: Date): void {
    this.userService.getUserMonthlyDetails(userId, shiftDate.toLocaleString('en-us', {month: 'long'}), shiftDate.getFullYear()).subscribe((workDetails: WorkDetails) => {
      // instead of pushing the new workDetails, we should update the existing one
      const employee = this.usersWithShifts.find(emp => emp.id === workDetails.userId);
      if (employee) {
        const key = this.generateCacheKey(shiftDate);
        this.workDetailsMap.get(workDetails.userId)?.set(key, workDetails);
        this.updateData();
      }
    });
  }

  deleteSchedule(): void {
    this.loading = true;
    const shiftDate = new Date(this.startDate);
    const cacheKey = this.generateCacheKey(shiftDate);
    const scheduleId = this.cachedSchedules[cacheKey].id;
    if (scheduleId == null) {
      return;
    }
    this.scheduleService.deleteSchedule(scheduleId).subscribe(() => {
      delete this.cachedSchedules[cacheKey];
      this.deleteMonthlyPlanFromMap(scheduleId);
      this.updateData();
      this.displayCreateScheduleButton = true;
    });
  }

  publishSchedule(): void {
    if (this.currentPlanId == null) {
      return;
    }
    this.scheduleService.publishSchedule(this.currentPlanId).subscribe(() => {
      this.messageService.add({severity: 'success', summary: 'Schedule published successfully'});
      this.cachedSchedules[this.generateCacheKey(this.startDate)].published = true;
      this.setCurrentSchedule();
    })
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

  getColorFromName(name: string): string {
    const colors = [
      '#FFBE0B', '#FB5607', '#FF006E', '#8338EC', '#3A86FF',
      '#1c8b71', '#26C6DA', '#E71D36', '#FF9A76', '#8AC926', '#1982C4'
    ];
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % colors.length;
    return colors[index];
  }

}

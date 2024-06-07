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
import {ShiftTypeService} from "../../services/shiftType.service";
import {ShiftType} from "../../interfaces/shiftType";
import {ShiftService} from "../../services/shift.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {MessageService} from "primeng/api";
import {ToastModule} from "primeng/toast";

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [
    WeekViewComponent,
    ToastModule
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
  monthNumber: number | undefined;

  constructor(private scheduleService: ScheduleService, private roleService: RolesService,
              private userService: UserService, private shiftTypeService: ShiftTypeService,
              private shiftService: ShiftService, private authorizationService: AuthorizationService,
              private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.fetchRoles();
    this.loadShiftTypes();
    this.getCurrentUser();
    this.loadSchedule();
  }

  loadSchedule(): void {
    this.startDate = this.getMondayOfCurrentWeek(new Date());
    this.startDate.setHours(0, 0, 0, 0);
    this.getUsersFromTeam();
    this.updateData();
  }

  updateData(): void {
    this.loading = true;
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
    const month = date.toLocaleString('en-us', {month: 'long'});
    const year = date.getFullYear();
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
    console.log(this.workDetailsMap);
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
        this.employeeShiftMap.get(employeeId)?.set(shiftId.toISOString(), shift);
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
        console.log(shift.date);
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
    let iterateDate = new Date(this.startDate);
    for (let i = 0; i < this.range; i++) {
      const dayName = this.getDayString(iterateDate);
      const date = new Date(iterateDate);
      days.push({dayName, date});
      iterateDate = new Date(iterateDate.setDate(iterateDate.getDate() + 1));
    }
    this.days = days;
  }

  async getDataIfNotCached(): Promise<void> {
    const currentDate = new Date(this.startDate);
    const endDate = new Date(this.startDate);
    endDate.setDate(this.startDate.getDate() + (this.range - 1));

    // Check if month data of first Day and last Day are already fetched
    await this.checkAndFetchSchedule(currentDate);
    await this.checkAndFetchSchedule(endDate);
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
        const date = new Date(day.date);
        date.setHours(date.getHours() + 2);
        const shift = this.employeeShiftMap.get(employee.id) && this.employeeShiftMap.get(employee.id)?.get(date.toISOString());
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
    const today = date;
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is Sunday
    return new Date(today.setDate(diff));
  }

  setWeekNumber(date: Date): void {
    // Copy date so that we don't modify the original date object
    const currentDate = new Date(date.getTime());

    // Set the date to the nearest Thursday: currentDate + 4 - currentDayNumber
    // Make Sunday (0) the last day of the week
    currentDate.setDate(currentDate.getDate() + 4 - (currentDate.getDay() || 7));

    // Get the first day of the year
    const yearStart = new Date(currentDate.getFullYear(), 0, 1);

    // Calculate the difference in milliseconds
    const diffInMs = currentDate.getTime() - yearStart.getTime();

    // Calculate full weeks to the nearest Thursday
    this.weekNumber = Math.ceil((((diffInMs / 86400000) + 1) / 7));
  }

  setMonthNumber(date: Date): void {
    // Get the month from the date object
    // Months are zero-based in JavaScript, so we add 1 to get a 1-based month number
    this.monthNumber = date.getMonth() + 1;
  }

  generateCacheKey(currentDate: Date) {
    const month = currentDate.toLocaleString('en-us', {month: 'long'});
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
      this.range = new Date(this.startDate.getFullYear(), this.startDate.getMonth() + 1, 0).getDate();
    } else {
      this.startDate.setDate(this.startDate.getDate() + (offset * this.range));
    }
    this.startDate.setHours(0, 0, 0, 0);
    this.setMonthNumber(this.startDate);
    this.setWeekNumber(this.startDate);
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
        this.currentPlanPublished = this.cachedSchedules[this.generateCacheKey(this.startDate)].published;
        this.currentPlanId = this.cachedSchedules[this.generateCacheKey(this.startDate)].id;
        break;
      default:
        throw new Error(`Unknown range: ${range}`);
    }
    this.setMonthNumber(this.startDate);
    this.setWeekNumber(this.startDate);
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

    const curEmployee = this.usersWithShifts.find(user => user.id === shiftInfo.user.id);

    switch (shiftInfo.operation.toLowerCase()) {
      case 'create':
        this.shiftService.createShift(shift).subscribe({
          next: (response) => {
            this.messageService.add({severity: 'success', summary: 'Successfully added shift'});
            shift.id = response.id;
            this.employeeShiftMap.get(response.users[0])?.set(shiftDate.toISOString(), shift);
            if (shiftInfo.user.id != undefined) {
              this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
            }
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Creating shift failed: ' + error.error().toString()});
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
            // delete curEmployee?.shifts[shiftDate.toDateString()];
            if (curEmployee?.id) {
              this.employeeShiftMap.get(curEmployee.id)?.delete(shiftDate.toISOString());
            }
            if (shiftInfo.user.id != undefined) this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Deleting shift failed: ' + error.toString()});
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
            this.employeeShiftMap.get(data.users[0])?.set(shiftDate.toISOString(), shift);
            if (shiftInfo.user.id != undefined) this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Updating shift failed: ' + error.toString()});
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

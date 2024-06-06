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

  constructor(private scheduleService: ScheduleService, private roleService: RolesService,
              private userService: UserService, private shiftTypeService: ShiftTypeService,
              private shiftService: ShiftService, private authorizationService: AuthorizationService,
              private messageService: MessageService) {
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
    this.loading = true;
    this.getDataIfNotCached();
    this.generateDays();
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
    const month = date.toLocaleString('en-us', {month: 'long'});
    const year = date.getFullYear();
    this.scheduleService.fetchSchedule(month, year).subscribe({
      next: data => {
        const cacheKey = this.generateCacheKey(date);
        if(data.id == null)
          return;
        this.cachedSchedules[cacheKey] = {id: data.id, published: data.published};
        this.setCurrentSchedule();
        this.userWorkDetails = data.monthlyWorkDetails;
        this.displayCreateScheduleButton = false;
        this.transformData(date, data.shifts, data.monthlyWorkDetails);
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

  setCurrentSchedule(): void {
    const cacheKey = this.generateCacheKey(this.startDate);
    this.currentSchedule = this.cachedSchedules[cacheKey];
  }

  mapUsers(users: User[]): UserWithShifts[] {
    return users.map(user => ({
      id: user.id, firstName: user.firstName, lastName: user.lastName,
      workingHoursPercentage: user.workingHoursPercentage, role: user.role, shifts: {}, workDetails: {}
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

  getDataIfNotCached(): void {
    const currentDate = new Date(this.startDate);
    const endDate = new Date(this.startDate);
    endDate.setDate(this.startDate.getDate() + (this.range - 1));

    // Check if month data of first Day and last Day are already fetched
    this.checkAndFetchSchedule(currentDate);
    this.checkAndFetchSchedule(endDate);
    this.setCurrentSchedule();
    this.loading = false;
  }

  checkAndFetchSchedule(date: Date): void {
    const cacheKey = this.generateCacheKey(date);
    if (!this.cachedSchedules[cacheKey]) {
      this.fetchMonthSchedule(date);
    }
  }

  transformData(calendarDate: Date, shifts: SimpleShift[], workDetails: WorkDetails[]): void {
    this.loading = true;
    // Iterate over shifts and parse the data, store in the respective employee's shifts map
    shifts.forEach(shift => {
      if (!shift.date) {
        return;
      }
      const date = new Date(shift.date).toDateString();
      const userId = shift.users[0]; // Accessing the single user in the array

      // Find the employee by id
      const employee = this.usersWithShifts.find(emp => emp.id === userId);
      if (employee) {
        employee.shifts[date] = {
          id: shift.id,
          date: shift.date,
          shiftType: shift.shiftType,
        };
      }
    });

    // Iterate over workDetails and add them to the respective employee's workDetails map
    workDetails.forEach(detail => {
      const employee = this.usersWithShifts.find(emp => emp.id === detail.userId);
      if (employee) {
        const monthYear = `${calendarDate.getMonth() + 1}/${calendarDate.getFullYear()}`; // Example format: "6/2023"
        employee.workDetails[monthYear] = detail;
      }
    });

    this.loading = false;
  }

  getMondayOfCurrentWeek(date: Date): Date {
    const today = date;
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is Sunday
    return new Date(today.setDate(diff));
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
    this.loading = true;
    this.displayCreateScheduleButton = false;
    this.currentWeekOffset += offset;
    if (this.range > 14) {
      this.startDate = new Date(this.startDate.getFullYear(), this.startDate.getMonth() + offset, 1);
      this.range = new Date(this.startDate.getFullYear(), this.startDate.getMonth()+1, 0).getDate();
    } else {
      this.startDate.setDate(this.startDate.getDate() + (offset * this.range));
    }
    this.startDate.setHours(0, 0, 0, 0);
    this.updateData()
  }

  changeRange(range: string): void {
    this.loading = true;
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
            if (curEmployee) {
              curEmployee.shifts[shiftDate.toDateString()] = shift;
            }
            if (shiftInfo.user.id != undefined) this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
            this.updateData();
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Creating shift failed: ' + error.toString()});
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
            delete curEmployee?.shifts[shiftDate.toDateString()];

            if (shiftInfo.user.id != undefined) this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
            this.updateData();
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
          next: () => {
            this.messageService.add({severity: 'success', summary: 'Successfully updated shift'});
            if (curEmployee?.shifts[shiftDate.toDateString()]) {
              curEmployee.shifts[shiftDate.toDateString()] = shift;
            }
            if (shiftInfo.user.id != undefined) this.fetchWorkDetails(shiftInfo.user.id, shiftDate);
            this.updateData();
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Updating shift failed: ' + error.toString()});
          }
        })
        break;
    }
  }

  fetchWorkDetails(userId: string, shiftDate: Date): void {
    this.userService.getUserMonthlyDetails(userId, shiftDate.toLocaleString('default', {month: 'long'}), shiftDate.getFullYear()).subscribe((workDetails: WorkDetails) => {
      // instead of pushing the new workDetails, we should update the existing one
      const employee = this.usersWithShifts.find(emp => emp.id === workDetails.userId);
      if (employee) {
        const monthYear = `${shiftDate.getMonth() + 1}/${shiftDate.getFullYear()}`; // Example format: "6/2023"
        employee.workDetails[monthYear] = workDetails;
      }
    });
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

  publishSchedule(): void {
    if (this.currentPlanId == null) {
      return;
    }
    this.scheduleService.publishSchedule(this.currentPlanId).subscribe( () => {
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

}

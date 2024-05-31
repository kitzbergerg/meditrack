import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Day, EmployeeWithShifts, RangeOption, Shift} from "../../../interfaces/schedule.models";
import {DatePipe, JsonPipe, NgClass, NgForOf, NgIf, NgStyle} from "@angular/common";
import {Table, TableModule} from "primeng/table";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";
import {ProgressSpinnerModule} from "primeng/progressspinner";
import {Role} from "../../../interfaces/role";
import {User} from "../../../interfaces/user";
import {OverlayPanelModule} from "primeng/overlaypanel";
import {ShiftType} from "../../../interfaces/shiftType";

@Component({
  selector: 'app-week-view',
  standalone: true,
  imports: [
    NgForOf,
    TableModule,
    NgStyle,
    NgIf,
    ButtonModule,
    InputTextModule,
    FormsModule,
    DropdownModule,
    ProgressSpinnerModule,
    DatePipe,
    OverlayPanelModule,
    JsonPipe,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './week-view.component.html',
  styleUrl: './week-view.component.scss'
})
export class WeekViewComponent implements OnChanges {

  @Input() loading = true;
  @Input() days: Day[] = [];
  @Input() employees: Map<string, EmployeeWithShifts> = new Map<string, EmployeeWithShifts>();
  @Input() startDate: Date | undefined;
  @Input() roles: Role[] | undefined;
  @Output() weekChange = new EventEmitter<number>();
  @Output() createSchedule = new EventEmitter<void>();
  @Output() rangeChange = new EventEmitter<string>();
  @Output() deleteSchedule = new EventEmitter<void>();
  @Output() updateShift = new EventEmitter<{
    user: User,
    day: Day,
    shiftType: ShiftType,
    shiftId: string | null,
    operation: string
  }>();
  @Input() displayCreateScheduleButton = false;
  @Input() users: User[] = [];
  @Input() shiftTypes: ShiftType[] = [];
  @Input() missingMonth = "";
  @Input() currentUser: User | undefined;
  weekNumber: number | undefined;
  monthNumber: number | undefined;
  currentShiftType: ShiftType | undefined;
  editing = false;


  range = 'week'; // Default value set to week = 7 days

  rangeOptions: RangeOption[] = [
    {label: 'Week', value: 'week'},
    {label: '2 Weeks', value: '2weeks'},
    {label: 'Month', value: 'month'}
  ];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes && this.startDate) {
      this.weekNumber = this.getWeekNumber(this.startDate);
      this.monthNumber = this.getMonthNumber(this.startDate);
    }
  }

  onGlobalFilter(table: Table, event: Event) {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }

  getWeekNumber(date: Date): number {
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
    return Math.ceil((((diffInMs / 86400000) + 1) / 7));
  }

  getDayStyle(user: User, day: Day) {
    if (user?.id == null) {
      return {
        'background-color': 'defaultColor',
        'color': '#fff'
      };
    }
    const shift = this.employees.get(user?.id)?.shifts?.[day.date.toDateString()];
    const shiftType = this.getShiftType(shift?.shiftType);
    const isWeekend = ['Su', 'Sa'].some(dayName => day.dayName.includes(dayName));
    const backgroundColor = shiftType?.color || (isWeekend ? 'lightgray' : 'defaultColor');
    return {
      'background-color': backgroundColor,
      'color': '#fff'
    };
  }

  getMonthNumber(date: Date): number {
    // Get the month from the date object
    // Months are zero-based in JavaScript, so we add 1 to get a 1-based month number
    return date.getMonth() + 1;
  }

  getShiftType(id: string | undefined): ShiftType | undefined {
    return this.shiftTypes.find((shiftType => shiftType.id && shiftType.id.toString() === id));
  }

  getShiftTypeFromDate(userId: string, date: Date): ShiftType | undefined {
    const shifts = this.employees?.get(userId)?.shifts;
    if (shifts) {
      return this.getShiftType(shifts[date.toDateString()]?.shiftType);
    }
    return undefined;
  }

  getShiftIdFromDate(userId: string, date: Date): string | null {
    const shifts = this.employees?.get(userId)?.shifts;
    if (shifts) {
      return shifts[date.toDateString()].id;
    }
    return null;
  }

  createNewSchedule(): void {
    this.createSchedule.emit();
  }

  previousWeek(): void {
    this.weekChange.emit(-1);
  }

  nextWeek(): void {
    this.weekChange.emit(1);
  }

  setRange(range: string): void {
    this.range = range;
    this.rangeChange.emit(range);
  }

  setShiftType(type: ShiftType | undefined): void {
    this.currentShiftType = type;
  }

  changeShift(user: User, day: Day, shiftId: string | null, operation: string): void {
    const shiftType = this.currentShiftType;
    if (shiftType)
      this.updateShift.emit({user, day, shiftType, shiftId, operation});
  }

  toggleEdit() {
    this.editing = !this.editing;
    this.setRange('month');
  }

  deleteMonthSchedule(): void {
    this.deleteSchedule.emit();
  }

  checkEditingAuthority(): boolean {
    if (!this.currentUser?.roles) {
      return false;
    }
    return this.currentUser.roles[0] === 'admin' || this.currentUser.roles[0] === 'dm';
  }

}

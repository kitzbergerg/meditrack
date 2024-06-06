import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import {
  Day,
  RangeOption, ScheduleWithId,
  UserWithShifts,
  WorkDetails
} from "../../../interfaces/schedule.models";
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
import {ConfirmationService, MessageService} from "primeng/api";
import {ConfirmDialogModule} from "primeng/confirmdialog";

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
    NgClass,
    ConfirmDialogModule,
  ],
  templateUrl: './week-view.component.html',
  styleUrl: './week-view.component.scss'
})
export class WeekViewComponent implements OnChanges {

  @Input() loading = true;
  @Input() days: Day[] = [];
  @Input() startDate: Date | undefined;
  @Input() roles: Role[] | undefined;
  @Input() employees: UserWithShifts[] = [];
  @Output() weekChange = new EventEmitter<number>();
  @Output() createSchedule = new EventEmitter<void>();
  @Output() rangeChange = new EventEmitter<string>();
  @Output() deleteSchedule = new EventEmitter<void>();
  @Output() updateShift = new EventEmitter<{
    user: UserWithShifts,
    day: Day,
    shiftType: ShiftType,
    shiftId: string | null,
    operation: string
  }>();
  @Output() publishSchedule = new EventEmitter<string>();
  @Input() displayCreateScheduleButton = false;
  @Input() users: User[] = [];
  @Input() shiftTypes: { [id: string]: ShiftType } = {};
  @Input() missingMonth = "";
  @Input() currentUser: User | undefined;
  @Input() planId: string | null = null;
  @Input() currentSchedule: ScheduleWithId | undefined;
  weekNumber: number | undefined;
  monthNumber: number | undefined;
  currentShiftType: ShiftType | null = null
  editing = false;

  range = 'week'; // Default value set to week = 7 days

  rangeOptions: RangeOption[] = [
    {label: 'Week', value: 'week'},
    {label: '2 Weeks', value: '2weeks'},
    {label: 'Month', value: 'month'}
  ];


  constructor(private messageService: MessageService, private confirmationService: ConfirmationService, private cdr: ChangeDetectorRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes && this.startDate) {
      this.weekNumber = this.getWeekNumber(this.startDate);
      this.monthNumber = this.getMonthNumber(this.startDate);
    }
  }

  trackByDay(index: number, day: any): string {
    return day.dayName; // Replace with the unique identifier of the day if available
  }

  trackByEmployeeId(index: number, employee: any): number {
    return employee.id; // or whatever unique identifier your data has
  }

  getWorkDetails(employee: UserWithShifts): WorkDetails | null {
    if (this.startDate == null) {
      return null;
    }
    return employee.workDetails[(this.startDate.getMonth()) + 1 + '/' + (this.startDate?.getFullYear())];
  }

  getShiftType(employee: UserWithShifts, day: Day): ShiftType | null {
    if (employee.shifts[day?.date.toDateString()] == null) {
      return null;
    }
    return this.shiftTypes[employee.shifts[day?.date.toDateString()].shiftType];
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

  getDayStyle(user: UserWithShifts, day: Day) {
    if (user?.id == null) {
      return {
        'background-color': 'defaultColor',
        'color': '#fff'
      };
    }
    const shiftType = this.getShiftType(user, day);
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

  createNewSchedule(): void {
    this.createSchedule.emit();
  }

  previousWeek(): void {
    this.weekChange.emit(-1);
  }

  nextWeek(): void {
    this.weekChange.emit(1);
  }

  getColorFromInitials(initials: string): string {
    const colors = [
      '#FFBE0B', '#FB5607', '#FF006E', '#8338EC', '#3A86FF',
      '#00C49A', '#9B51E0', '#FF4F19', '#26C6DA', '#FF9F1C',
      '#2EC4B6', '#E71D36', '#FF9A76', '#8AC926', '#1982C4'
    ];
    let hash = 0;
    for (let i = 0; i < initials.length; i++) {
      hash = initials.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % colors.length;
    return colors[index];
  }

  setRange(range: string): void {
    this.range = range;
    this.rangeChange.emit(range);
  }

  setShiftType(type: ShiftType | null): void {
    if (type == null) {
      this.currentShiftType = null;
    }
    this.currentShiftType = type;
  }

  changeShift(user: UserWithShifts, day: Day, operation: string): void {
    const shiftType = this.currentShiftType;
    if (shiftType) {
      const shiftId = user.shifts[day.date.toDateString()]?.id || null;
      this.updateShift.emit({user, day, shiftType, shiftId, operation});
    }
  }

  toggleEdit() {
    this.editing = !this.editing;
    this.setRange('month');
  }

  deleteMonthSchedule(): void {
    this.deleteSchedule.emit();
  }

  publishMonthSchedule(): void {
    this.editing = false;
    this.publishSchedule.emit()
  }

  checkEditingAuthority(): boolean {
    if (!this.currentUser?.roles) {
      return false;
    }
    return this.currentUser.roles[0] === 'admin' || this.currentUser.roles[0] === 'dm';
  }

  confirmDelete(event: Event) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to delete this schedule?',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",

      accept: () => {
        this.deleteMonthSchedule();
      }
    });
  }

  protected readonly Object = Object;
}

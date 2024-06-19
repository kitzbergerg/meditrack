import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output,} from '@angular/core';
import {Day, RangeOption, ScheduleWithId, ShiftWithIds, UserWithShifts} from "../../../interfaces/schedule.models";
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
import {format, startOfDay} from 'date-fns';
import {SickLeaveDialogComponent} from "../sick-leave-dialog/sick-leave-dialog.component";

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
    SickLeaveDialogComponent,
  ],
  templateUrl: './week-view.component.html',
  styleUrl: './week-view.component.scss'
})
export class WeekViewComponent implements OnInit {

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
  @Input() weekNumber: number | undefined;
  @Input() monthString: string | undefined;
  currentShiftType: ShiftType | null = null
  editing = false;
  protected readonly Object = Object;
  range = 'week'; // Default value set to week = 7 days
  todaysDate: Date | undefined;
  sickShift: ShiftWithIds | null = null;
  sickDay: Day | null = null;
  displayDialog = false;

  rangeOptions: RangeOption[] = [
    {label: 'Week', value: 'week'},
    {label: '2 Weeks', value: '2weeks'},
    {label: 'Month', value: 'month'}
  ];


  constructor(private messageService: MessageService, private confirmationService: ConfirmationService, private cdr: ChangeDetectorRef) {
  }


  ngOnInit(): void {
    this.todaysDate = startOfDay(new Date());
  }

  trackByDay(index: number, day: Day): string {
    return day.dayName;
  }

  trackByShift(index: number, shift: ShiftWithIds): string {
    return shift?.shiftType?.toString() || "";
  }

  onGlobalFilter(table: Table, event: Event) {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }

  createNewSchedule(): void {
    this.range = 'month';
    this.createSchedule.emit();
    this.editing = true;
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

  setShiftType(type: ShiftType | null): void {
    if (type == null) {
      this.currentShiftType = null;
    }
    this.currentShiftType = type;
  }

  changeShift(user: UserWithShifts, i: number, day: Day, operation: string): void {
    const shiftType = this.currentShiftType;
    if (shiftType) {
      const shiftId = user.shifts[i]?.id || null;
      this.updateShift.emit({user, day, shiftType, shiftId, operation});
    }
  }

  handleUpdateShift(shiftInfo: { user: UserWithShifts, day: Day, shiftType: ShiftType, shiftId: string | null, operation: string }) {
    this.updateShift.emit(shiftInfo);
  }

  toggleEdit() {
    this.editing = !this.editing;
    this.setRange('month');
  }

  deleteMonthSchedule(): void {
    this.editing = false;
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
      acceptIcon: "pi pi-trash mr-2",
      rejectIcon: "none",

      accept: () => {
        this.deleteMonthSchedule();
      }
    });
  }

  confirmPublish(event: Event) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to publish this schedule?',
      header: 'Publish Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-success p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "pi pi-check mr-2",
      rejectIcon: "none",

      accept: () => {
        this.publishMonthSchedule();
      }
    });
  }

  sickLeave(shift : ShiftWithIds, day: Day) {
    this.sickShift = shift;
    this.sickDay = day;
    this.displayDialog = true;
  }

  protected readonly format = format;
}

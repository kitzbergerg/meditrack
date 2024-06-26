import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
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
import {PdfGenerationService} from "../../../services/pdf-generation.service";
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
    isSick: boolean,
    operation: string
  }>();
  @Output() publishSchedule = new EventEmitter<string>();
  @Input() displayCreateScheduleButton = false;
  @Input() shiftTypes: { [id: string]: ShiftType } = {};
  @Input() missingMonth = "";
  @Input() currentUser: User | undefined;
  @Input() currentSchedule: ScheduleWithId | undefined;
  @Input() weekNumber: number | undefined;
  @Input() monthString: string | undefined;
  currentShiftType: ShiftType | null = null
  editing = false;
  range = 'week'; // Default value set to week = 7 days
  todaysDate: Date | undefined;
  sickShift: ShiftWithIds | null = null;
  sickDay: Day | null = null;
  displayDialog = false;
  fileredDay: Day | null = null;
  rangeOptions: RangeOption[] = [
    {label: 'Week', value: 'week'},
    {label: '2 Weeks', value: '2weeks'},
    {label: 'Month', value: 'month'}
  ];


  constructor(private messageService: MessageService,
              private confirmationService: ConfirmationService,
              private pdfGenerationService: PdfGenerationService) {
  }


  ngOnInit(): void {
    this.todaysDate = startOfDay(new Date());
  }

  toggleTodaysShifts(day: Day): void {
    if (this.fileredDay !== day) {
      this.fileredDay = day;
      this.employees.sort((a, b) => {
        const hasShiftA = a.shifts.some(shift => shift && shift.date === format(day.date, 'yyyy-MM-dd'));
        const hasShiftB = b.shifts.some(shift => shift && shift.date === format(day.date, 'yyyy-MM-dd'));

        if (hasShiftA && !hasShiftB) {
          return -1;
        }
        if (!hasShiftA && hasShiftB) {
          return 1;
        }
        return 0;
      });
    } else {
      this.employees = this.employees.sort((a, b) => {
        if (a.id === this.currentUser?.id) {
          return -1;
        }
        if (b.id === this.currentUser?.id) {
          return 1;
        }
        return a.lastName.localeCompare(b.lastName);
      });
      this.fileredDay = null;
    }
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

  changeShift(user: UserWithShifts, i: number, day: Day, isSick: boolean, operation: string): void {
    const shiftType = this.currentShiftType;
    if (shiftType) {
      const shiftId = user.shifts[i]?.id || null;
      this.updateShift.emit({user, day, shiftType, shiftId, isSick, operation});
    }
  }

  handleUpdateShift(shiftInfo: {
    user: UserWithShifts,
    day: Day,
    shiftType: ShiftType,
    shiftId: string | null,
    isSick: boolean,
    operation: string
  }) {
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


  sickLeave(shift: ShiftWithIds, day: Day) {
    this.sickShift = shift;
    this.sickDay = day;
    this.displayDialog = true;
  }

  hideDialog(): void {
    this.sickShift = null;
    this.sickDay = null;
    this.displayDialog = false;
  }

  protected readonly format = format;
  protected readonly Object = Object;

  generatePdf(day: Day) {


    this.pdfGenerationService.downloadPdf(day.date.toLocaleString('default', { month: 'long' }), day.date.getFullYear()).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = 'monthly_plan.pdf';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
    }, error => {
      console.error('Error downloading PDF:', error);
      if (error.status === 404) {
        this.messageService.add({ severity: 'error', summary: 'No monthly plan created yet!'});
      } else {
        this.messageService.add({ severity: 'error', summary: 'error creating pdf: ' + error.error});
      }
    });
  }
}

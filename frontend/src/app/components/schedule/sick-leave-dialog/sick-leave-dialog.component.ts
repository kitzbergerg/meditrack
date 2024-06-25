import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {UserService} from "../../../services/user.service";
import {DialogModule} from "primeng/dialog";
import {ButtonModule} from "primeng/button";
import {User} from "../../../interfaces/user";
import {InputTextModule} from "primeng/inputtext";
import {DatePipe, NgForOf, NgIf, NgStyle} from "@angular/common";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {ListboxModule} from "primeng/listbox";
import {Day, ShiftWithIds, UserWithShifts, WorkDetails} from "../../../interfaces/schedule.models";
import {ShiftType} from "../../../interfaces/shiftType";

@Component({
  selector: 'app-sick-leave-dialog',
  standalone: true,
  imports: [
    DialogModule,
    ButtonModule,
    InputTextModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    ListboxModule,
    NgStyle,
    DatePipe
  ],
  templateUrl: './sick-leave-dialog.component.html',
  styleUrl: './sick-leave-dialog.component.scss'
})
export class SickLeaveDialogComponent implements OnChanges, OnInit {

  @Input({required: true}) shift!: ShiftWithIds | null;
  @Input({required: true}) day!: Day | null;
  @Input({required: true}) employees!: UserWithShifts[] | null;
  @Input({required: true}) displayDialog = false;
  @Output() hideDialog = new EventEmitter<void>();
  @Output() updateShift = new EventEmitter<{
    user: UserWithShifts,
    day: Day,
    shiftType: ShiftType,
    shiftId: string | null,
    isSick: boolean,
    operation: string
  }>();
  replacements: User[] = [];
  formGroup!: FormGroup;

  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.formGroup = new FormGroup({
      selectedReplacement: new FormControl<User | null>(null)
    });
  }

  ngOnChanges(changes:SimpleChanges): void {
    if (this.shift) {
      this.displayDialog = true;
      this.getReplacementForShift(this.shift);
    }
  }

  getReplacementForShift(shift: ShiftWithIds): void {
    const shiftId: string | null = shift.id;
    if (!shiftId) {
      return;
    }
    this.userService.getReplacementsForShift(shiftId).subscribe({
      next: (response) => {
        this.replacements = response.sort((a, b) => {
          const workDetailsA = this.getWorkingDetails(a);
          const workDetailsB = this.getWorkingDetails(b);
          const overtimeA = workDetailsA ? workDetailsA.overtime : 0;
          const overtimeB = workDetailsB ? workDetailsB.overtime : 0;
          return overtimeA - overtimeB;
        });
      }
    });
  }

  getWorkingDetails(user: User): WorkDetails | null {
      return this.employees?.find((employee) => employee.id === user.id)?.workDetails || null;
  }

  sendUpdateShift() {
    if (!this.shift?.shiftType || !this.formGroup.get('selectedReplacement')?.value || !this.day || !this.hideDialog) {
      return;
    }
    const event = {
      user: this.formGroup.get('selectedReplacement')?.value,
      day: this.day,
      shiftType: this.shift?.shiftType,
      shiftId: null,
      isSick: false,
      operation: 'create'
    };

    this.updateShift.emit(event);
    this.formGroup.reset();
    this.shift = null;
    this.replacements = [];
    this.hideDialog.emit();
  }

  hide() {
    if (this.hideDialog) {
      this.hideDialog.emit();
    }
  }
}

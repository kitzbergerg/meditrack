import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {UserService} from "../../../services/user.service";
import {DialogModule} from "primeng/dialog";
import {ButtonModule} from "primeng/button";
import {User} from "../../../interfaces/user";
import {InputTextModule} from "primeng/inputtext";
import {DatePipe, NgForOf, NgIf, NgStyle} from "@angular/common";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {ListboxModule} from "primeng/listbox";
import {Day, ShiftWithIds, UserWithShifts} from "../../../interfaces/schedule.models";
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
  @Output() updateShift = new EventEmitter<{
    user: UserWithShifts,
    day: Day,
    shiftType: ShiftType,
    shiftId: string | null,
    operation: string
  }>();
  replacements: User[] = [];
  formGroup!: FormGroup;

  displayDialog = false;

  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.formGroup = new FormGroup({
      selectedReplacement: new FormControl<User | null>(null)
    });
  }

  ngOnChanges(): void {
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
        console.log(response);
        this.replacements = response;
      }
    });
  }

  sendUpdateShift() {
    if (!this.shift?.shiftType || !this.formGroup.get('selectedReplacement')?.value || !this.day) {
      return;
    }
    const event = {
      user: this.formGroup.get('selectedReplacement')?.value,
      day: this.day,
      shiftType: this.shift?.shiftType,
      shiftId: null,
      operation: 'create'
    };
    this.updateShift.emit(event);
    this.formGroup.reset();
    this.shift = null;
    this.replacements = [];
    this.hideDialog();
  }

  hideDialog(): void {
    this.displayDialog = false;
  }

}

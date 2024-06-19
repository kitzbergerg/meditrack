import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {UserService} from "../../../services/user.service";
import {DialogModule} from "primeng/dialog";
import {ButtonModule} from "primeng/button";
import {User} from "../../../interfaces/user";
import {InputTextModule} from "primeng/inputtext";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-sick-leave-dialog',
  standalone: true,
  imports: [
    DialogModule,
    ButtonModule,
    InputTextModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './sick-leave-dialog.component.html',
  styleUrl: './sick-leave-dialog.component.scss'
})
export class SickLeaveDialogComponent implements OnChanges {

  @Input({ required: true }) shiftId!: string | null;
  replacements: User[] = [];

  displayDialog = false;

  constructor(private userService: UserService) {
  }

  ngOnChanges(): void {
    if (this.shiftId) {
      this.displayDialog = true;
      this.getReplacementForShift(this.shiftId);
    }
  }

  getReplacementForShift(shiftId: string): void {
    this.userService.getReplacementsForShift(shiftId).subscribe({
      next: (response) => {
        console.log(response);
        this.replacements = response;
      }
    });
  }

  hideDialog(): void {
    this.displayDialog = false;
  }

}

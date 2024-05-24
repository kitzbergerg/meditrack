import {Component, EventEmitter, Input, Output} from '@angular/core';
import {RulesService} from "../../../services/rules.service";
import {ButtonModule} from "primeng/button";
import {InputNumberModule} from "primeng/inputnumber";
import {NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-max-shift-lengths',
  standalone: true,
  imports: [
    ButtonModule,
    InputNumberModule,
    NgIf,
    RippleModule,
    FormsModule
  ],
  templateUrl: './max-shift-lengths.component.html',
  styleUrl: './max-shift-lengths.component.scss'
})
export class MaxShiftLengthsComponent {
  @Input() maxShiftLength: number | null = null;
  editMode = true;
  @Output() updateMaxShiftLength = new EventEmitter<number | null>();
  duration: number | null = this.maxShiftLength;

  constructor() {
    if (this.maxShiftLength == null) {
      //this.maxShiftLength = {duration: 1};
      //this.duration = 1;
    }
  }

  update() {
    this.editMode = false
    this.updateMaxShiftLength.emit(this.duration)
  }

  delete() {
    this.updateMaxShiftLength.emit(null)
  }

}

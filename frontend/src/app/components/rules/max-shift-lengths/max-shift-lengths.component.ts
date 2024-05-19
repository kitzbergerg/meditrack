import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MaxShiftLength} from "../../../interfaces/rules/rulesInterface";
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
  @Input() maxShiftLength: MaxShiftLength | null = null;
  editMode = true;
  @Output() deleteMaxShiftLength = new EventEmitter<void>();
  duration: number | undefined = this.maxShiftLength?.duration;

  constructor(rulesService: RulesService) {
    if (this.maxShiftLength == null) {
      this.maxShiftLength = {duration: 1};
    }
  }

  update() {
    this.editMode = false
    console.log("")
  }

  delete() {
    this.deleteMaxShiftLength.emit()
  }

}

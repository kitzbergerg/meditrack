import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MinRestPeriod} from "../../../interfaces/rules/rulesInterface";
import {RulesService} from "../../../services/rules.service";
import {ButtonModule} from "primeng/button";
import {InputNumberModule} from "primeng/inputnumber";
import {NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-allowed-flex-time-per-month',
  standalone: true,
  imports: [
    ButtonModule,
    InputNumberModule,
    NgIf,
    RippleModule,
    FormsModule
  ],
  templateUrl: './allowed-flex-time-per-month.component.html',
  styleUrl: './allowed-flex-time-per-month.component.scss'
})
export class AllowedFlexTimePerMonthComponent {
  @Input() allowedFlexTimePerMonth: number | null = null;
  editMode = true;
  @Output() deleteAllowedFlexTimePerMonth = new EventEmitter<void>();

  constructor(rulesService: RulesService) {
    if (this.allowedFlexTimePerMonth == null) {
      this.allowedFlexTimePerMonth = 1;
    }
  }

  update() {
    this.editMode = false
    console.log("")
  }

  delete() {
    this.deleteAllowedFlexTimePerMonth.emit()
  }

}

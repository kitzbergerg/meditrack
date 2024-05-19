import {Component, EventEmitter, Input, Output} from '@angular/core';
import {PaginatorModule} from "primeng/paginator";
import {NgIf} from "@angular/common";
import {MandatoryOffDays, Rules} from "../../../interfaces/rules/rulesInterface";
import {RulesService} from "../../../services/rules.service";
import {ButtonModule} from "primeng/button";
import {RippleModule} from "primeng/ripple";

@Component({
  selector: 'app-mandatory-off-days-rule',
  standalone: true,
  imports: [
    PaginatorModule,
    NgIf,
    ButtonModule,
    RippleModule,
  ],
  templateUrl: './mandatory-off-days-rule.component.html',
  styleUrl: './mandatory-off-days-rule.component.scss'
})
export class MandatoryOffDaysRuleComponent {
  @Input() mandatoryOffDays: MandatoryOffDays | null = null;
  editMode = true;
  @Output() deleteMandatoryOffDaysRule = new EventEmitter<void>();
  numberOfDaysInMonth: number | undefined = this.mandatoryOffDays?.numberOfDaysInMonth;

  constructor(rulesService: RulesService) {
    console.log(this.mandatoryOffDays);
    if (this.mandatoryOffDays == null) {
      this.mandatoryOffDays = {numberOfDaysInMonth: 1};
    }
    console.log(this.mandatoryOffDays);
  }

  update() {
    this.editMode = false
    console.log("")
  }

  delete() {
    this.deleteMandatoryOffDaysRule.emit()
  }
}

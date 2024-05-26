import {Component, EventEmitter, Input, Output} from '@angular/core';
import {PaginatorModule} from "primeng/paginator";
import {NgIf} from "@angular/common";
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
  @Input() mandatoryOffDays: number | null = null;
  editMode = false;
  @Output() updateMandatoryOffDays = new EventEmitter<number | null>();

  constructor() {
    console.log(this.mandatoryOffDays);
    if (this.mandatoryOffDays == null) {
      this.mandatoryOffDays = 1;
    }
  }

  update() {
    this.editMode = false
    this.updateMandatoryOffDays.emit(this.mandatoryOffDays)
  }

  delete() {
    this.updateMandatoryOffDays.emit(null)
  }
}

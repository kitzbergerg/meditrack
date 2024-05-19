import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ButtonModule} from "primeng/button";
import {InputNumberModule} from "primeng/inputnumber";
import {NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {MinRestPeriod} from "../../../interfaces/rules/rulesInterface";
import {RulesService} from "../../../services/rules.service";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-min-rest-period-rule',
  standalone: true,
  imports: [
    ButtonModule,
    InputNumberModule,
    NgIf,
    RippleModule,
    FormsModule
  ],
  templateUrl: './min-rest-period-rule.component.html',
  styleUrl: './min-rest-period-rule.component.scss'
})
export class MinRestPeriodRuleComponent {
  @Input() minRestPeriod: MinRestPeriod | null = null;
  editMode = true;
  @Output() deleteMinRestPeriod = new EventEmitter<void>();
  duration: number | undefined = this.minRestPeriod?.duration;

  constructor(rulesService: RulesService) {
    if (this.minRestPeriod == null) {
      this.minRestPeriod = {duration: 1};
    }
  }

  update() {
    this.editMode = false
    console.log("")
  }

  delete() {
    this.deleteMinRestPeriod.emit()
  }

}

import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {PaginatorModule} from "primeng/paginator";
import {Rules} from "../../interfaces/rules/rulesInterface";
import {RulesService} from "../../services/rules.service";
import {ButtonModule} from "primeng/button";
import {RippleModule} from "primeng/ripple";
import {MandatoryOffDaysRuleComponent} from "./mandatory-offdays-rule/mandatory-off-days-rule.component";
import {MinRestPeriodRuleComponent} from "./min-rest-period-rule/min-rest-period-rule.component";
import {MaxShiftLengthsComponent} from "./max-shift-lengths/max-shift-lengths.component";

@Component({
  selector: 'app-rules',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    PaginatorModule,
    ButtonModule,
    RippleModule,
    MandatoryOffDaysRuleComponent,
    MinRestPeriodRuleComponent,
    MaxShiftLengthsComponent
  ],
  templateUrl: './rules.component.html',
  styleUrl: './rules.component.scss'
})
export class RulesComponent {
  rules: Rules | null = null;

  showAvailableRules = false;
  showMandatoryOffDaysRule = false;
  showMinRestPeriod = false;
  showMaxShiftLengths= false;

  constructor(rulesService: RulesService) {
    rulesService.getRules().subscribe((x: Rules) => this.rules = x)
  }

  anyRulesNotSet() {
    return this.rules?.minRestPeriod == null
      || this.rules?.maxShiftLengths == null
      || this.rules?.mandatoryOffDays == null;
  }
  deleteMandatoryOffDaysRule() {
    this.showMandatoryOffDaysRule = false;
    this.rules!.mandatoryOffDays = null;
    //update()
  }

  deleteMinRestPeriodRule() {
    this.showMandatoryOffDaysRule = false;
    this.rules!.minRestPeriod = null;
  }

  deleteMaxShiftLengthRule() {
    this.showMandatoryOffDaysRule = false;
    this.rules!.maxShiftLengths = null;
  }
}

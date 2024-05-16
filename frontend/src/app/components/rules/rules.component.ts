import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {PaginatorModule} from "primeng/paginator";
import {MandatoryOffDays, Rules} from "../../interfaces/rules/rulesInterface";
import {RulesService} from "../../services/rules.service";
import {ButtonModule} from "primeng/button";
import {RippleModule} from "primeng/ripple";

@Component({
  selector: 'app-rules',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    PaginatorModule,
    ButtonModule,
    RippleModule
  ],
  templateUrl: './rules.component.html',
  styleUrl: './rules.component.scss'
})
export class RulesComponent {
  rules: Rules | null = null;
  editMode = false;
  editedRule = ''
  editMinRestPeriod = false;
  editMaximumShiftLength = false;
  editMandatoryOffDays = false;
  showAvailbleRules = false;

  constructor(rulesService: RulesService) {
    rulesService.getRules().subscribe((x: Rules) => this.rules = x)
  }

  editRule(rule: string) {
    this.editedRule = rule;
    this.editMode = true;
  }

  anyRulesNotSet() {
    return this.rules?.minRestPeriod == null
      || this.rules?.maximumShiftLengths == null
      || this.rules?.mandatoryOffDays == null;
  }

  updateRules() {
    console.log("updateRules");
  }

  createMandatoryOffDaysRule() {
    this.rules!.mandatoryOffDays =  {numberOfDaysInMonth: 1};
    this.editMandatoryOffDays = true;
  }
}

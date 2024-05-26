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
import {DayTimeRequiredRolesComponent} from "./day-time-required-roles/day-time-required-roles.component";
import {NightTimeRequiredRolesComponent} from "./night-time-required-roles/night-time-required-roles.component";
import {AllowedFlexTimeTotalComponent} from "./allowed-flex-time-total/allowed-flex-time-total.component";
import {AllowedFlexTimePerMonthComponent} from "./allowed-flex-time-per-month/allowed-flex-time-per-month.component";
import {Role} from "../../interfaces/role";


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
    MaxShiftLengthsComponent,
    DayTimeRequiredRolesComponent,
    NightTimeRequiredRolesComponent,
    AllowedFlexTimeTotalComponent,
    AllowedFlexTimePerMonthComponent
  ],
  templateUrl: './rules.component.html',
  styleUrl: './rules.component.scss'
})
export class RulesComponent {
  rules: Rules | null = null;

  showAvailableRules = false;
  showMandatoryOffDaysRule = false;
  showMinRestPeriod = false;
  showMaxShiftLengths = false;
  showDayTimeRequiredRoles = false;
  showNightTimeRequiredRoles = false;
  showAllowedFlexTimeTotal = false;
  showAllowedFlexTimePerMonth = false;

  constructor(private rulesService: RulesService) {
    rulesService.getRules().subscribe({
      next: (x: Rules) => {
        if (x == null) {
          this.rules = this.emptyRules()
          console.log('null', this.rules);
        } else {
          this.rules = x
          console.log('not null', this.rules);
          if (this.rules.mandatoryOffDays) {
            this.showMandatoryOffDaysRule = true
          }
          if (this.rules.minRestPeriod) {
            this.showMinRestPeriod = true
          }
          if (this.rules.maxShiftLengths) {
            this.showMaxShiftLengths = true
          }
          if (this.rules.daytimeRequiredRoles) {
            this.showDayTimeRequiredRoles = true
          }
          if (this.rules.nighttimeRequiredRoles) {
            this.showNightTimeRequiredRoles = true
          }
          if (this.rules.allowedFlextimeTotal) {
            this.showAllowedFlexTimeTotal = true
          }
          if (this.rules.allowedFlextimePerMonth) {
            this.showAllowedFlexTimePerMonth = true
          }
        }
        console.log(this.rules);
      },
      error: (err) => {
        console.log('Error getting role:', err)
        this.rules = this.emptyRules()
      }
    })
  }

  emptyRules() {
    return {
      shiftOffShift: null,
      minRestPeriod: null,
      maxShiftLengths: null,
      mandatoryOffDays: null,
      daytimeRequiredRoles: null,
      nighttimeRequiredRoles: null,
      allowedFlextimeTotal: null,
      allowedFlextimePerMonth: null
    }
  }

  anyRulesNotSet() {
    return this.rules?.minRestPeriod == null
      || this.rules?.maxShiftLengths == null
      || this.rules?.mandatoryOffDays == null;
  }

  save() {
    console.log("save", this.rules)
    this.rulesService.saveRules(this.rules!).subscribe({
        next: (response) => {
          console.log('Rules created successfully:', response);
        },
        error: (error) => {
          console.error('Error creating rule:', error);
        }
      }
    )
  }


  updateMandatoryOffDaysRule(mandatoryOffDays: number | null) {
    console.log('updated', mandatoryOffDays)
    if (mandatoryOffDays == null) {
      this.showMandatoryOffDaysRule = false;
    }
    this.rules!.mandatoryOffDays = mandatoryOffDays;
    this.save()
  }

  updateMinRestPeriodRule(minRest: number | null) {
    if (minRest == null) {
      this.showMinRestPeriod = false;
    }
    this.rules!.minRestPeriod = minRest;
    this.save()
  }

  updateMaxShiftLengthRule(maxShift: number | null) {
    if (maxShift == null) {
      this.showMaxShiftLengths = false;
    }
    this.rules!.maxShiftLengths = maxShift;
    this.save()
  }

  updateDayTimeRequiredRolesRule(dayTimeRoles: [Role | null, number][] | null) {
    if (dayTimeRoles == null) {
      this.showDayTimeRequiredRoles = false;
    }
    this.rules!.daytimeRequiredRoles = dayTimeRoles;
    this.save()
  }

  updateNightTimeRequiredRolesRule(nightTimeRoles: [Role | null, number][] | null) {
    if (nightTimeRoles == null) {
      this.showNightTimeRequiredRoles = false;
    }
    this.rules!.nighttimeRequiredRoles = nightTimeRoles;
    this.save()
  }

  updateAllowedFlexTimeTotalRule(flexTotal: number | null) {
    if (flexTotal == null) {
      this.showAllowedFlexTimeTotal = false;
    }
    this.rules!.allowedFlextimeTotal = flexTotal;
    this.save()
  }

  updateAllowedFlexTimePerMonthRule(flexMonth: number | null) {
    if (flexMonth == null) {
      this.showAllowedFlexTimePerMonth = false;
    }
    this.rules!.allowedFlextimePerMonth = flexMonth;
    this.save()
  }
}

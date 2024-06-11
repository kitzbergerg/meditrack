import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {PaginatorModule} from "primeng/paginator";
import {Rules} from "../../interfaces/rules";
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
     /* next: (x: Rules) => {
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
          if (this.rules.maximumShiftLengths) {
            this.showMaxShiftLengths = true
          }
          if (this.rules.daytimeRequiredRoles !== null){
            this.showDayTimeRequiredRoles = true;
          }

          if (this.rules.nighttimeRequiredRoles !== null){
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
      }*/
    })
  }

  emptyRules() {
    return {
      shiftOffShift: null,
      minRestPeriod: null,
      maximumShiftLengths: null,
      mandatoryOffDays: null,
      daytimeRequiredRoles: null,
      nighttimeRequiredRoles: null,
      allowedFlextimeTotal: null,
      allowedFlextimePerMonth: null
    }
  }

  anyRulesNotSet() {
    return !this.showAllowedFlexTimePerMonth || !this.showAllowedFlexTimeTotal || !this.showDayTimeRequiredRoles
      || !this.showMandatoryOffDaysRule || !this.showMaxShiftLengths || !this.showMinRestPeriod  ||
      !this.showNightTimeRequiredRoles
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
    this.rules!.maximumShiftLengths = maxShift;
    this.save()
  }

  updateDayTimeRequiredRolesRule(dayTimeRoles: [Role | null, number][] | null) {
    if (dayTimeRoles == null) {
      this.showDayTimeRequiredRoles = false;
      this.rules!.daytimeRequiredRoles = new Map<number, number>();
      this.save()
    } else {
      const dayRoleMap: Map<number, number> = new Map<number, number>();
      for (const dayTimeRole of dayTimeRoles) {
        if (dayTimeRole != null && dayTimeRole[0] != null) {
          dayRoleMap.set(dayTimeRole[0]!.id!, dayTimeRole[1])
        }
      }
      this.rules!.daytimeRequiredRoles = Object.fromEntries(dayRoleMap.entries());
      console.log('dayRoleMap', dayRoleMap)
      console.log('this.rules!.daytimeRequiredRoles', this.rules!.daytimeRequiredRoles)
      this.save()
    }
  }

  updateNightTimeRequiredRolesRule(nightTimeRoles: [Role | null, number][] | null) {
    if (nightTimeRoles == null) {
      this.showNightTimeRequiredRoles = false;
      this.rules!.nighttimeRequiredRoles = new Map<number, number>();
      this.save()
    } else {
      const nightRoleMap: Map<number, number> = new Map<number, number>();
      for (const nightRole of nightTimeRoles) {
        if (nightRole != null && nightRole[0] != null) {
          nightRoleMap.set(nightRole[0]!.id!, nightRole[1])
        }
      }
      this.rules!.nighttimeRequiredRoles = Object.fromEntries(nightRoleMap.entries());
      console.log('nightRoleMap', nightRoleMap)
      console.log('this.rules!.nighttimeRequiredRoles', this.rules!.nighttimeRequiredRoles)
      this.save()
    }
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

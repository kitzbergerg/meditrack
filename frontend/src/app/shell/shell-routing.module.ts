import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {AccountSettingsComponent} from "../components/account-settings/account-settings.component";
import {ShellComponent} from "./shell/shell.component";
import {EmployeesComponent} from "../components/employees/employees.component";
import {RolesComponent} from "../components/roles/roles.component";
import {DashboardComponent} from "../components/dashboard/dashboard.component";
import {employeeGuard} from "../guard/employee.guard";
import {dmGuard} from "../guard/dm.guard";
import {ShiftTypesComponent} from "../components/shift-types/shift-types.component";
import {ScheduleComponent} from "../components/schedule/schedule.component";
import {ShiftSwapComponent} from "../components/shift-swap/shift-swap.component";
import {HolidaysComponent} from "../components/holidays/holidays.component";
import {RulesComponent} from "../components/rules/rules.component";

const routes: Routes = [
  {
    path: 'dashboard',
    canActivate: [employeeGuard],
    component: DashboardComponent,
  },
  {
    path: 'employees',
    canActivate: [dmGuard],
    component: EmployeesComponent,
  },
  {
    path: 'roles',
    canActivate: [dmGuard],
    component: RolesComponent,
  },
  {
    path: 'shift-types',
    canActivate: [dmGuard],
    component: ShiftTypesComponent,
  },
  {
    path: 'account-settings',
    component: AccountSettingsComponent,
  },
  {
    path: 'schedule',
    canActivate: [employeeGuard],
    component: ScheduleComponent,
  },
  {
    path: 'holidays',
    component: HolidaysComponent,
  },
  {
    path: 'rules',
    canActivate: [dmGuard],
    component: RulesComponent,
  },
  {
    path: 'shift-swap',
    canActivate: [employeeGuard],
    component: ShiftSwapComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild([
      {
        path: '',
        pathMatch: 'full',
        children: routes
      },
      {
        path: '',
        component: ShellComponent,
        data: {reuse: false}, // Reuse ShellComponent instance when navigating between child views
        children: routes
      },
    ]
  )],
})
export class ShellRoutingModule {
}

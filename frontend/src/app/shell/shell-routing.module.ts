import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {AccountSettingsComponent} from "../components/account-settings/account-settings.component";
import {ShellComponent} from "./shell/shell.component";
import {EmployeesComponent} from "../components/employees/employees.component";
import {RolesComponent} from "../components/roles/roles.component";
import {DashboardComponent} from "../components/dashboard/dashboard.component";
import {employeeGuard} from "../guard/employee.guard";
import {dmGuard} from "../guard/dm.guard";
import {RulesComponent} from "../components/rules/rules.component";
import {ShiftTypesComponent} from "../components/shift-types/shift-types.component";

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
    path: 'rules',
    canActivate: [dmGuard],
    component: RulesComponent,
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

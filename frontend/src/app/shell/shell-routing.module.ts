import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {ShellRedirectGuard} from "../guard/shell-redirection.guard";
import {AccountSettingsComponent} from "../components/account-settings/account-settings.component";
import {ShellComponent} from "./shell/shell.component";
import {EmployeesComponent} from "../components/employees/employees.component";
import {RolesComponent} from "../components/roles/roles.component";
import {DashboardComponent} from "../components/dashboard/dashboard.component";
import {employeeGuard} from "../guard/employee.guard";
import {dmGuard} from "../guard/dm.guard";
import {AppComponent} from "../app.component";

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
    path: 'account-settings',
    component: AccountSettingsComponent,
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

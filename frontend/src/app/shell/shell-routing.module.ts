import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {ShellRedirectGuard} from "../guard/shell-redirection.guard";
import {
  DepartmentManagerDashboardComponent
} from "../components/department-manager-dashboard/department-manager-dashboard.component";
import {EmployeeDashboardComponent} from "../components/employee-dashboard/employee-dashboard.component";
import {AccountSettingsComponent} from "./account-settings/account-settings.component";
import {ShellComponent} from "./shell/shell.component";
import {EmployeesComponent} from "../components/employees/employees.component";
import {RolesComponent} from "../components/roles/roles.component";

const routes: Routes = [
  {
    path: 'department-manager-dashboard',
    component: DepartmentManagerDashboardComponent,
  },
  {
    path: 'employee-dashboard',
    component: EmployeeDashboardComponent,
  },
  {
    path: 'employees',
    component: EmployeesComponent,
  },
  {
    path: 'roles',
    component: RolesComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild([{
      path: '',
      pathMatch: 'full',
      canActivate: [ShellRedirectGuard],
      children: routes
    },
      {
        path: 'account-settings',
        component: AccountSettingsComponent,
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

import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {ShellRedirectGuard} from "../guard/shell-redirection.guard";
import {
  DepartmentManagerDashboardComponent
} from "../components/department-manager-dashboard/department-manager-dashboard.component";
import {EmployeeDashboardComponent} from "../components/employee-dashboard/employee-dashboard.component";
import {AccountSettingsComponent} from "../components/account-settings/account-settings.component";

@NgModule({
  imports: [RouterModule.forChild([{
      path: '',
      pathMatch: 'full',
      canActivate: [ShellRedirectGuard],
      children: []
    },
    {
      path: 'department-manager-dashboard',
      component: DepartmentManagerDashboardComponent,
    },
    {
      path: 'employee-dashboard',
      component: EmployeeDashboardComponent,
    },
    {
      path: 'account-settings',
      component: AccountSettingsComponent,
    },
    ]
  )],
})
export class ShellRoutingModule {
}

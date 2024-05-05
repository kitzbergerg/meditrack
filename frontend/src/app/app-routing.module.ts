import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AuthGuard} from "./guard/auth.guard";
import {LoginComponent} from "./components/login/login.component";
import { DepartmentManagerDashboardComponent } from './components/department-manager-dashboard/department-manager-dashboard.component';
import {EmployeeDashboardComponent} from "./components/employee-dashboard/employee-dashboard.component";

const routes: Routes = [

  {
    path: '',
    component: LoginComponent,
    canActivate: [AuthGuard],
    loadChildren: () => import('./shell/shell.module').then((m) => m.ShellModule),
  },
  {
    path: 'emp',
    component: EmployeeDashboardComponent,
  },
  {
    path: 'dm',
    component: DepartmentManagerDashboardComponent,
    canActivate: [AuthGuard],
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

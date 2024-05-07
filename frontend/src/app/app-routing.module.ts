import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AuthGuard} from "./guard/authentication.guard";
import {LoginComponent} from "./components/login/login.component";
import { DepartmentManagerDashboardComponent } from './components/department-manager-dashboard/department-manager-dashboard.component';

const routes: Routes = [

  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'test',
    component: DepartmentManagerDashboardComponent
  },
  {
    path: '',
    canActivate: [AuthGuard],
    loadChildren: () => import('./shell/shell.module').then((m) => m.ShellModule),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

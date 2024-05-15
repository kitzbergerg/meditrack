import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AuthGuard} from "./guard/authentication.guard";
import {LoginComponent} from "./components/login/login.component";
import {ErrorPageComponent} from "./components/error-page/error-page.component";
import {AppLayoutComponent} from "./layout/app.layout.component";

const routes: Routes = [

  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    canActivate: [AuthGuard],
    component: AppLayoutComponent,
    loadChildren: () => import('./shell/shell.module').then((m) => m.ShellModule),
  },
  {
    path: '**',
    component: ErrorPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

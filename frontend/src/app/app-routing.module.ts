import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AuthGuard} from "./guard/authentication.guard";

const routes: Routes = [
  //TODO LOGIN

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

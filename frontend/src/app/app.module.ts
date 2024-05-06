import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LangComponent } from './lang/lang.component';
import { DepartmentManagerDashboardComponent } from './components/department-manager-dashboard/department-manager-dashboard.component';
import { RouterModule } from '@angular/router';
import { EmployeeDashboardComponent } from './components/employee-dashboard/employee-dashboard.component';
import { LoginComponent } from './components/login/login.component';
import { AccountSettingsComponent } from './components/account-settings/account-settings.component';
import { ToolbarComponent } from './components/toolbar/toolbar.component';
import {ButtonModule} from "primeng/button";

@NgModule({
  declarations: [
    AppComponent,
    LangComponent,
    DepartmentManagerDashboardComponent,
    EmployeeDashboardComponent,
    LoginComponent,
    AccountSettingsComponent,
    ToolbarComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        RouterModule,
        ButtonModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

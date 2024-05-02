import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // Import FormsModule

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LangComponent } from './lang/lang.component';
import { DepartmentManagerDashboardComponent } from './components/department-manager-dashboard/department-manager-dashboard.component';
import { RouterModule } from '@angular/router';
import { EmployeeDashboardComponent } from './components/employee-dashboard/employee-dashboard.component';
import { LoginComponent } from './components/login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { OAuthModule} from 'angular-oauth2-oidc';


@NgModule({
  declarations: [
    AppComponent,
    LangComponent,
    DepartmentManagerDashboardComponent,
    EmployeeDashboardComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    HttpClientModule,
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: ['localhost:8081/api/user'],
        sendAccessToken: true,
      },
    }),
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LangComponent } from './lang/lang.component';
import { DepartmentManagerDashboardComponent } from './components/department-manager-dashboard/department-manager-dashboard.component';
import { RouterModule } from '@angular/router';
import { EmployeeDashboardComponent } from './components/employee-dashboard/employee-dashboard.component';
import { LoginComponent } from './components/login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { OAuthModule} from 'angular-oauth2-oidc';
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import { ButtonModule } from 'primeng/button';
import {StyleClassModule} from "primeng/styleclass";

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'meditrack',
        clientId: 'web',
      },
      initOptions: {
        scope: 'openid',
        onLoad: 'check-sso',
        enableLogging: true,
        checkLoginIframe: false,
        flow: 'standard',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html'
      },
    });
}

@NgModule({
  declarations: [
    AppComponent,
    LangComponent,
    DepartmentManagerDashboardComponent,
    EmployeeDashboardComponent,
    LoginComponent,
  ],
  imports: [
    BrowserModule,
    ButtonModule,
    AppRoutingModule,
    RouterModule,
    HttpClientModule,
    KeycloakAngularModule,
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: ['localhost:8081/api/user'],
        sendAccessToken: true,
      },
    }),
    FormsModule,
    StyleClassModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }

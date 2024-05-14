import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HttpClientModule } from '@angular/common/http';
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import { ButtonModule } from 'primeng/button';
import {StyleClassModule} from "primeng/styleclass";
import {AccountSettingsComponent} from './components/account-settings/account-settings.component';
import {EmployeesComponent} from './components/employees/employees.component';
import {RolesComponent} from './components/roles/roles.component';
import {DashboardComponent} from "./components/dashboard/dashboard.component";
import {RippleModule} from "primeng/ripple";
import {AppLayoutModule} from "./layout/app.layout.module";
import {RadioButtonModule} from "primeng/radiobutton";
import {DialogModule} from "primeng/dialog";
import {DropdownModule} from "primeng/dropdown";
import {ToastModule} from "primeng/toast";
import {ToolbarModule} from "primeng/toolbar";
import {TableModule} from "primeng/table";
import {InputNumberModule} from "primeng/inputnumber";


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
    LoginComponent,
    AccountSettingsComponent,
    EmployeesComponent,
    RolesComponent,
    DashboardComponent,
  ],
  imports: [
    BrowserModule,
    ButtonModule,
    AppRoutingModule,
    RouterModule,
    HttpClientModule,
    KeycloakAngularModule,
    FormsModule,
    StyleClassModule,
    RippleModule,
    AppLayoutModule,
    RadioButtonModule,
    DialogModule,
    DropdownModule,
    ToastModule,
    ToolbarModule,
    TableModule,
    InputNumberModule
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
export class AppModule {
}

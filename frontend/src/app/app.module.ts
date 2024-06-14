import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
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
import {ToolbarModule} from "primeng/toolbar";
import {TableModule} from "primeng/table";
import {DialogModule} from "primeng/dialog";
import {DropdownModule} from "primeng/dropdown";
import {InputTextModule} from "primeng/inputtext";
import {ImageModule} from "primeng/image";
import {ShiftTypesComponent} from "./components/shift-types/shift-types.component";
import { ColorPickerModule } from 'primeng/colorpicker';
import { CalendarModule } from "primeng/calendar";
import {MultiSelectModule} from "primeng/multiselect";
import {ToastModule} from "primeng/toast";
import {ConfirmationService, MessageService} from 'primeng/api';
import {TeamComponent} from "./components/team/team.component";
import {ChipModule} from "primeng/chip";
import {InputNumberModule} from "primeng/inputnumber";
import {ShiftSwapComponent} from "./components/shift-swap/shift-swap.component";
import {CardModule} from "primeng/card";
import {ConfirmDialogModule} from "primeng/confirmdialog";


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
    ShiftTypesComponent,
    TeamComponent,
    ShiftSwapComponent
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
    ReactiveFormsModule,
    ToolbarModule,
    TableModule,
    DialogModule,
    DropdownModule,
    InputTextModule,
    ImageModule,
    CalendarModule,
    ColorPickerModule,
    MultiSelectModule,
    ToastModule,
    ChipModule,
    InputNumberModule,
    CardModule,
    ConfirmDialogModule,
  ],
  providers: [
    MessageService,
    ConfirmationService,
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

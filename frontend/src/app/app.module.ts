import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LangComponent } from './lang/lang.component';
import {I18nSTUFF} from "../I18n";

@NgModule({
  declarations: [
    AppComponent,
    LangComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [I18nSTUFF.setLocale(), I18nSTUFF.setLocaleId()],
  bootstrap: [AppComponent]
})
export class AppModule { }

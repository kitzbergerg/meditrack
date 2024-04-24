/// <reference types="@angular/localize" />

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import {TRANSLATIONS, TRANSLATIONS_FORMAT} from "@angular/core";
import german from './locale/messages.de.json';
import english from './locale/messages.en.json';
import {loadTranslations} from "@angular/localize";


/*
platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
*/


if (localStorage.getItem('locale') === null) {
  localStorage.setItem('locale', 'en');
}

const locale = localStorage.getItem('locale');

// eslint-disable-next-line @typescript-eslint/no-var-requires
const translations = locale == 'en' ? english : german;
console.log('main',locale)
loadTranslations((translations))


platformBrowserDynamic().bootstrapModule(AppModule, {
  providers: [
    {provide: TRANSLATIONS, useValue: translations},
    {provide: TRANSLATIONS_FORMAT, useValue: 'json'}
  ]
}).catch(err => console.error(err));

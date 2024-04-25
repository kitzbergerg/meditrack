// eslint-disable-next-line @typescript-eslint/triple-slash-reference
/// <reference types="@angular/localize" />

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import german from './locale/messages.de.json';
import english from './locale/messages.en.json';
import {loadTranslations} from "@angular/localize";


platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));


if (localStorage.getItem('locale') === null) {
  localStorage.setItem('locale', 'en');
}
const locale = localStorage.getItem('locale');
let translations = english;
if (locale == 'de') {
  translations = german;
}
loadTranslations((translations))

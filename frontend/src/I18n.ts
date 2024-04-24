import {APP_INITIALIZER, Injectable, LOCALE_ID, TRANSLATIONS} from "@angular/core";
import {loadTranslations} from "@angular/localize";
import english from "./locale/messages.en.json";
import german from "./locale/messages.de.json";

@Injectable({providedIn: "root"})
class I18n {
  locale: any;

  async setLocale() {
    const translations = this.locale == 'en' ? english : german;
    console.log(this.locale)
    //loadTranslations((translations))
  }
}

function setLocale() {
  return {
    provide: APP_INITIALIZER,
    useFactory: (i18n: I18n) => () => i18n.setLocale(),
    deps: [I18n],
    multi: true,
  }
}

function setLocaleId() {
  return {
    provide: LOCALE_ID,
    useFactory: (i18n: I18n) => i18n.locale,
    deps: [I18n],
  }
}

export const I18nSTUFF = {
  setLocale: setLocale,
  setLocaleId: setLocaleId,
}

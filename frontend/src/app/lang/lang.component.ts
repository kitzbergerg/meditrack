import { Component } from '@angular/core';


/*
* use 'ng extract-i18n' in terminal to generate i18n keys for all i18n fields
*/
@Component({
  selector: 'app-lang',
  templateUrl: './lang.component.html',
  styleUrls: ['./lang.component.scss']
})
export class LangComponent {

  changeLang(lang: string) {
    if (lang === 'de') {
      localStorage.setItem('locale', 'de');
    }

    if (lang === 'en') {
      localStorage.setItem('locale', 'en');
    }
  }
}

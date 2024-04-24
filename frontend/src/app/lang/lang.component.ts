import { Component } from '@angular/core';

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

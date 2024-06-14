import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Preferences} from "../interfaces/preferences";

@Injectable({
  providedIn: 'root'
})
export class PreferencesService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/preferences';

  getPreferences(id: string): Observable<Preferences> {
    return this.http.get<Preferences>(this.apiUrl+`/${id}`);
  }

  createPreferences(preference: Preferences): Observable<Preferences> {
    return this.http.post<Preferences>(`${this.apiUrl}`, preference);
  }

  updatePreferences(preference: Preferences): Observable<Preferences> {
    return this.http.put<Preferences>(`${this.apiUrl}`, preference);
  }

  deletePreferences(id: string) {
    return this.http.delete(this.apiUrl+`/${id}`);
  }
}

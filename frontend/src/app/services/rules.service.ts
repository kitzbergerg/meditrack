import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Rules} from "../interfaces/rules";

@Injectable({
  providedIn: 'root'
})
export class RulesService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/rules';

  getRules(): Observable<any> {
    return this.http.get<any>(this.apiUrl)
  }

  saveRules(rules: Rules): Observable<Rules> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    return this.http.post<Rules>(this.apiUrl, rules, httpOptions);
  }
}

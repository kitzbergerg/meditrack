import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Rules} from "../interfaces/rules/rulesInterface";

@Injectable({
  providedIn: 'root'
})
export class RulesService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/role';

  getRules(): Observable<Rules> {
    return this.http.get<Rules>(this.apiUrl)
  }
}

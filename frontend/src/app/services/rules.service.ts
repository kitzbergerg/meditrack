import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Rules} from "../interfaces/rules";
import {RoleRules, Rule} from "../interfaces/rule";

@Injectable({
  providedIn: 'root'
})
export class RulesService {
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/rules';

  getRules(): Observable<any> {
    return this.http.get<any>(this.apiUrl)
  }

  saveRules(rules: Rules): Observable<Rules> {

    return this.http.post<Rules>(this.apiUrl, rules, this.httpOptions);
  }

  getAllRulesFromTeam(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}`, this.httpOptions);
  }

  updateRule(rule: Rule): Observable<Rule> {
    return this.http.put<Rule>(`${this.apiUrl}/rules/`, rule, this.httpOptions);
  }

  getRulesFromRole(ruleId: number): Observable<RoleRules> {
    return this.http.get<RoleRules>(`${this.apiUrl}/rules/${ruleId}`, this.httpOptions);
  }

  updateRoleRule(roleRules: RoleRules) {
    return this.http.put<RoleRules>(`${this.apiUrl}/roleRules/${roleRules.role.id}`, roleRules, this.httpOptions);
  }
}

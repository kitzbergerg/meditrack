import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {HardConstraintsDto, RoleRules, Rule} from "../interfaces/rule";

@Injectable({
  providedIn: 'root'
})
export class RulesService {
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/rules';

  getAllRulesFromTeam(): Observable<HardConstraintsDto> {
    return this.http.get<HardConstraintsDto>(`${this.apiUrl}`, this.httpOptions);
  }

  saveRules(rules: Rule[]): Observable<HardConstraintsDto> {
    const dto: HardConstraintsDto = {
      workingHours: null,
      maxWeeklyHours: null,
      maxConsecutiveShifts: null,
      daytimeRequiredPeople: null,
      nighttimeRequiredPeople: null,
    };

    rules.forEach(rule => {
      if (rule.name in dto) {
        dto[rule.name as keyof HardConstraintsDto] = rule.value;
      }
    });
   return this.http.post<HardConstraintsDto>(this.apiUrl, dto, this.httpOptions);

  }

  getAllRoleRulesFromTeam(): Observable<RoleRules[]> {
    return this.http.get<RoleRules[]>(`${this.apiUrl}/roleRules`, this.httpOptions);
  }

  updateRoleRule(roleRules: RoleRules) {
    return this.http.put<RoleRules>(`${this.apiUrl}/roleRules`, roleRules, this.httpOptions);
  }
}

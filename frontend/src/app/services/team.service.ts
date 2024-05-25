import { Injectable } from '@angular/core';
import {User} from "../interfaces/user";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Team} from "../interfaces/team";

@Injectable({
  providedIn: 'root'
})
export class TeamService {


  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/team';



  getAllTeams(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
  getTeamById(id: string): Observable<Team> {
    return this.http.get<Team>(`${this.apiUrl}/${id}`);
  }

  createTeam(team: Team): Observable<Team> {
    return this.http.post<Team>(`${this.apiUrl}`, team);
  }
}
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import {User} from "../interfaces/user";
import {WorkDetails} from "../interfaces/schedule.models";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/user';
  private userinfoUrl = 'http://localhost:8080/realms/meditrack/protocol/openid-connect/userinfo';
  private realmUrl = "http://localhost:8080/realms/meditrack";


  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getAllUserFromTeam(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/team`);
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}?shouldSendInviteMail=true`, user);
  }

  deleteUser(user: User): Observable<NonNullable<unknown>> {
    return this.http.delete(`${this.apiUrl}/${user.id}`);
  }

  updateUser(user: User): Observable<User> {
    user.username = undefined;
    return this.http.put<User>(this.apiUrl, user);
  }

  getUserMonthlyDetails(userId: string, month: string, year: number): Observable<WorkDetails> {
    return this.http.get<WorkDetails>(`${this.apiUrl}/monthly-details?userId=${userId}&month=${month}&year=${year}`);
  }

  getReplacementsForShift(shiftId: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/replacement/${shiftId}`);
  }

}

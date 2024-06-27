import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Holiday} from "../interfaces/holiday";

@Injectable({
  providedIn: 'root'
})
export class HolidaysService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/holiday';

  createHoliday(holiday: Holiday): Observable<Holiday> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };

    return this.http.post<Holiday>(`${this.apiUrl}?shouldSendMail=true`, holiday, httpOptions);
  }

  updateHoliday(holiday: Holiday): Observable<Holiday> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };

    return this.http.put<Holiday>(this.apiUrl, holiday, httpOptions);
  }

  updateHolidayStatus(id: string, status: string): Observable<Holiday> {
    return this.http.put<Holiday>(this.apiUrl + `/${id}/${status}?shouldSendMail=true`, null);
  }

  getAllHolidaysByUser(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(this.apiUrl);
  }

  getHolidayByIdAndUser(id: string): Observable<Holiday> {
    return this.http.get<Holiday>(this.apiUrl + `/${id}`);
  }

  getAllHolidaysByTeam(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(this.apiUrl + '/team');
  }

  getAllHolidays(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(this.apiUrl + '/all');
  }

  deleteHoliday(id: string) {
    return this.http.delete(this.apiUrl + `/${id}`);
  }
}

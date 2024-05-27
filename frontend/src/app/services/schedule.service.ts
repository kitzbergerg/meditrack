import {Injectable} from "@angular/core";

import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Schedule} from "../interfaces/schedule.models";

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {

  constructor(private http: HttpClient) {
  }

  private baseUrl = 'http://localhost:8081/api/monthly-plan';

  createSchedule(month: string, year: number): Observable<Schedule> {
    //TODO: param should not be required in backend
    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month);

    const url = `${this.baseUrl}`;
    return this.http.post<Schedule>(url, "", {params});
  }

  fetchSchedule(month: string, year: number): Observable<Schedule> {
    //TODO: param should not be required in backend
    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month);

    const url = `${this.baseUrl}/team`;
    return this.http.get<Schedule>(url, {params});
  }

}

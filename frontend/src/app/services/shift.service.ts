import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SimpleShift} from "../interfaces/schedule.models";

@Injectable({
  providedIn: 'root'
})
export class ShiftService {

  constructor(private http: HttpClient) {
  }

  private apiUrl = 'http://localhost:8081/api/shift';

  createShift(shift: SimpleShift): Observable<SimpleShift> {
    const url = `${this.apiUrl}`;
    console.log(JSON.stringify(shift));
    return this.http.post<SimpleShift>(url, shift);
  }

  deleteShift(shiftId: string) {
    const url = `${this.apiUrl}/${shiftId}`;
    return this.http.delete(url);
  }

  updateShift(shift: SimpleShift): Observable<SimpleShift> {
    return this.http.put<SimpleShift>(this.apiUrl, shift);
  }
}

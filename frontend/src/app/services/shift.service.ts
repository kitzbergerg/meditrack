import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {ShiftType} from "../interfaces/shiftType";

@Injectable({
  providedIn: 'root'
})
export class ShiftService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/shift-type';

  getAllShiftTypes(): Observable<ShiftType[]> {
    return this.http.get<ShiftType[]>(this.apiUrl);
  }

  getAllShiftTypesByTeam(): Observable<ShiftType[]> {
    return this.http.get<ShiftType[]>(this.apiUrl + '/team');
  }

  getShiftType(id: number): Observable<ShiftType> {
    return this.http.get<ShiftType>(this.apiUrl+`/${id}`).pipe(
        map((shiftType) => {
          return shiftType;
        }));
  }

  createShiftType(shiftType: ShiftType): Observable<ShiftType> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };

    return this.http.post<ShiftType>(this.apiUrl, shiftType, httpOptions);
  }

  updateShiftType(shiftType: ShiftType): Observable<ShiftType> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };

    return this.http.put<ShiftType>(this.apiUrl, shiftType, httpOptions);
  }

  deleteShiftType(id: number) {
    return this.http.delete(this.apiUrl+`/${id}`);
  }
}

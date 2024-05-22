import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {ShiftType, ShiftTypeCreate} from "../interfaces/shiftTypeInterface";

@Injectable({
  providedIn: 'root'
})
export class ShiftService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/shift-type';

  getAllShiftTypes(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  getShiftType(id: number): Observable<ShiftType> {
    return this.http.get<ShiftType>(this.apiUrl+`/${id}`).pipe(
        map((shiftType) => {
          return shiftType;
        }));
  }

  createShiftType(shiftType: ShiftTypeCreate): Observable<ShiftType> {
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

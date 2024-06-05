import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ShiftSwap, ShiftSwapShift, SimpleShiftSwap} from "../interfaces/shiftSwap";
import {Shift} from "../interfaces/schedule.models";

@Injectable({
  providedIn: 'root'
})
export class ShiftSwapService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/shift-swap';
  private shiftUrl = 'http://localhost:8081/api/shift';

  getAllRequestedShiftSwaps(): Observable<ShiftSwap[]> {
    return this.http.get<ShiftSwap[]>(this.apiUrl);
  }

  createShiftSwap(shiftSwap: SimpleShiftSwap): Observable<ShiftSwap> {
    return this.http.post<ShiftSwap>(`${this.apiUrl}`, shiftSwap);
  }

  updateShiftSwap(shiftSwap: ShiftSwap): Observable<ShiftSwap> {
    return this.http.put<ShiftSwap>(`${this.apiUrl}`, shiftSwap);
  }

  getAllShiftsFromCurrentMonth() {
    return this.http.get<ShiftSwapShift[]>(this.shiftUrl + '/month');
  }
}

import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ShiftSwap, ShiftSwapShift} from "../interfaces/shiftSwap";

@Injectable({
  providedIn: 'root'
})
export class ShiftSwapService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/shift-swap';
  private shiftUrl = 'http://localhost:8081/api/shift';

  getAllShiftSwaps(): Observable<ShiftSwap[]> {
    return this.http.get<ShiftSwap[]>(this.apiUrl);
  }

  getAllOwnShiftSwapOffers(): Observable<ShiftSwap[]> {
    return this.http.get<ShiftSwap[]>(this.apiUrl + '/own-offers');
  }

  getAllShiftSwapRequests(): Observable<ShiftSwap[]> {
    return this.http.get<ShiftSwap[]>(this.apiUrl + '/requests');
  }

  getAllShiftSwapSuggestions(): Observable<ShiftSwap[]> {
    return this.http.get<ShiftSwap[]>(this.apiUrl + '/suggestions');
  }

  getAllOfferedShiftSwaps(): Observable<ShiftSwap[]> {
    return this.http.get<ShiftSwap[]>(this.apiUrl + '/offers');
  }

  createShiftSwap(shiftSwap: ShiftSwap): Observable<ShiftSwap> {
    return this.http.post<ShiftSwap>(`${this.apiUrl}?shouldSendMail=true`, shiftSwap);
  }

  updateShiftSwap(shiftSwap: ShiftSwap): Observable<ShiftSwap> {
    return this.http.put<ShiftSwap>(`${this.apiUrl}?shouldSendMail=true`, shiftSwap);
  }

  deleteShiftSwap(id: string) {
    return this.http.delete(this.apiUrl+`/${id}`);
  }

  retractShiftSwapRequest(id: string): Observable<ShiftSwap> {
    return this.http.delete<ShiftSwap>(`${this.apiUrl}` + '/retract' + `/${id}`);
  }

  getAllShiftsFromCurrentMonth() {
    return this.http.get<ShiftSwapShift[]>(this.shiftUrl + '/month');
  }
}

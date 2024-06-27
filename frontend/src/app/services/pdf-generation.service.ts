import { Injectable } from '@angular/core';
import {map, Observable} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Role} from "../interfaces/role";

@Injectable({
  providedIn: 'root'
})
export class PdfGenerationService {

  private apiUrl = 'http://localhost:8081/api/pdf';

  constructor(private http: HttpClient) { }

  downloadPdf(month: string, year: number): Observable<Blob> {

    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month);
    return this.http.get(`${this.apiUrl}`, { responseType: 'blob' , params: params });
  }
}

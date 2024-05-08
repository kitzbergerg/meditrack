import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class RolesService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/role';

  getAllRoles(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

}

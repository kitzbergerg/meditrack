import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/user';
  private userinfoUrl = 'http://localhost:8080/realms/meditrack/protocol/openid-connect/userinfo';
  private accessToken = '';


  getAllUsers(): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.accessToken}`
    });
    return this.http.get<any>(this.apiUrl, { headers });
  }
  getUserById(id: string) {
    return this.http.get(this.apiUrl+`/${id}`);
  }

}

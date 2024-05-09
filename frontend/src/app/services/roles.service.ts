import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Role, RoleCreate} from "../interfaces/roles/rolesInterface";

@Injectable({
  providedIn: 'root'
})
export class RolesService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8081/api/role';

  getAllRoles(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  createRole(role: RoleCreate): Observable<Role> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };

    return this.http.post<Role>(this.apiUrl, role, httpOptions);
  }

  updateRole(role: Role): Observable<Role> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };

    return this.http.put<Role>(this.apiUrl, role, httpOptions);
  }

  deleteRole(id: number) {
    return this.http.delete(this.apiUrl+`/${id}`);
  }
}

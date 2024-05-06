import { Component } from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Component({
  selector: 'app-department-manager-dashboard',
  templateUrl: './department-manager-dashboard.component.html',
  styleUrls: ['./department-manager-dashboard.component.scss']
})
export class DepartmentManagerDashboardComponent {

  private apiUrl = 'http://localhost:8081/api/user';
  private userinfoUrl = 'http://localhost:8080/realms/meditrack/protocol/openid-connect/userinfo';
  private accessToken = ''; // Replace with your actual Access Token
  data: any;
  userinfo: any;
  constructor(private authorizationService: AuthorizationService, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.getToken()
    console.log(this.authorizationService.isLoggedIn());
    //console.log(this.authorizationService.userName)
  }

  dGet(): void {
    this.getUserData().subscribe(
      (response) => {
        this.data = response;
        console.log(this.data)
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );}


  async getToken() {
    await this.authorizationService.token.then(r => {
      this.accessToken = r;
    })
  }

  async getUser() {
    this.getUserKeycloakInfo().subscribe(
      userinfo => this.userinfo = userinfo,
      error => console.log('Error fetching userinfo:', error)
    );
  }

  getUserName() {
    this.getUser().then(r => {if (this.userinfo != undefined) {
      console.log(this.userinfo.name)
    }})
  }

  getUserData(): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.accessToken}`
    });
    return this.http.get<any>(this.apiUrl, { headers });
  }

  getUserKeycloakInfo() {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.accessToken}`
    });
    return this.http.get<any>(this.userinfoUrl, { headers });
  }


}

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
  data: any;
  constructor(private authorizationService: AuthorizationService, private http: HttpClient) {
  }

  ngOnInit(): void {
    console.log(this.authorizationService.isLoggedIn());
    //console.log(this.authorizationService.userName)
  }

  dGet(): void {
    this.getData().subscribe(
      (response) => {
        this.data = response;
        console.log(this.data)
        // Handle response data
      },
      (error) => {
        console.error('Error fetching data:', error);
        // Handle error
      }
    );}

  getData(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  userinfo: any;

  async getUser() {
    this.accessToken = await this.authorizationService.token
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

  private userinfoUrl = 'http://localhost:8080/realms/meditrack/protocol/openid-connect/userinfo';
  private accessToken = ''; // Replace with your actual Access Token


  getUserKeycloakInfo() {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.accessToken}`
    });

    return this.http.get<any>(this.userinfoUrl, { headers });
  }


}

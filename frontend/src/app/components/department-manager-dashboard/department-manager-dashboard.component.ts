import { Component } from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-department-manager-dashboard',
  templateUrl: './department-manager-dashboard.component.html',
  styleUrls: ['./department-manager-dashboard.component.scss']
})
export class DepartmentManagerDashboardComponent {

  data: any;
  userId = '';

  constructor(private authorizationService: AuthorizationService, private http: HttpClient, private userService: UserService) {
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe(
      (response) => {
        this.data = response;
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );
  }
}

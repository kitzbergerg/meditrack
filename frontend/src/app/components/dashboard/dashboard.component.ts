import { Component } from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
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

import {Component} from '@angular/core';
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../services/user.service";


@Component({
  selector: 'app-account-settings',
  templateUrl: './account-settings.component.html',
  styleUrls: ['./account-settings.component.scss']
})
export class AccountSettingsComponent {

  data: any;
  userId = '';

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()
  }

  constructor(private authorizationService: AuthorizationService, private http: HttpClient, private userService: UserService) {
  }


  logout() {
    this.authorizationService.logout();
  }

  changePassword() {
    this.authorizationService.changePassword().then();
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

import {Component} from '@angular/core';
import { Router } from '@angular/router';
import {AuthorizationService} from "../../services/authorization/authorization.service";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  constructor(private authorizationService: AuthorizationService, private router: Router) {
  }


  login() {
    this.authorizationService.login().then();
  }

  ngOnInit(): void {
    if (this.authorizationService.isLoggedIn()) {
      console.log(this.authorizationService.parsedToken().sub);
    }else{
      console.log("not logged in");
    }
  }

  isLoggedIn() {
    return this.authorizationService.isLoggedIn()
  }

  logout() {
    this.authorizationService.logout();
  }
}

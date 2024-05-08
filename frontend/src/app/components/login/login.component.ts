import {Component} from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {


  constructor(private authorizationService: AuthorizationService, private router: Router, private authenticationService: AuthenticationService) {
  }

  loginStub(role: string) {
    console.log('loginStub')
    this.authenticationService.login(role)
      .subscribe(res => {
        if (role == 'employer') {
          void this.router.navigate(['department-manager-dashboard'])
        } else if (role == 'employee') {
          void this.router.navigate(['employee-dashboard'],)
        }
      });
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

import {Component} from '@angular/core';
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService) {
  }

  loginStub(role: string) {
    console.log('loginStub')
    this.authenticationService.login(role)
      .subscribe(res => {
        if (role == 'employer') {
          void this.router.navigate(['department-manager-dashboard'])
        } else if (role == 'employee') {
          void this.router.navigate(['employee-dashboard'],  )
        }
      });
  }
}

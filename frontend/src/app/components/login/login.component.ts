import {Component} from '@angular/core';

import { JwksValidationHandler, OAuthService } from 'angular-oauth2-oidc';
import {AuthorizationService} from "../../services/authentication/authorization.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {


  constructor(private oauthService: OAuthService, private authorizationService: AuthorizationService) {
  }

  ngOnInit(): void {
    if (this.authorizationService.isLoggedIn()) {
      console.log(this.authorizationService.parsedToken().sub)
    }
  }

  logout() {
    this.authorizationService.logout();
    //this.oauthService.logOut();
  }

  get userName() {
    const claims = this.oauthService.getIdentityClaims();
    if (!claims) return null;
    return claims['preferred_username'];
  }

  get isLoggedIn() {
    return this.oauthService.hasValidAccessToken();
  }
}

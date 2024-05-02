import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {Router} from "@angular/router";
import { JwksValidationHandler, OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from 'src/app/auth.config';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  token: string = "";

  constructor(private oauthService: OAuthService) {
  }

  ngOnInit(): void{
    this.configureWithNewConfigApi();
  }

  private configureWithNewConfigApi() {
    this.oauthService.configure(authConfig);
    this.oauthService.tokenValidationHandler = new JwksValidationHandler();
    this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  login() {
    this.oauthService.initCodeFlow();
  }


  logout() {
    this.oauthService.logOut();
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

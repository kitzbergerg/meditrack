import {KeycloakEventType, KeycloakService} from "keycloak-angular";

import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})

export class AuthorizationService {


  constructor(private keycloakService: KeycloakService) {
  }

  parsedToken(): any {
    return this.keycloakService.getKeycloakInstance().tokenParsed;
  }

  login() {
    return this.keycloakService.login({redirectUri: "http://localhost:4200/dashboard", });
  }

  changePassword() {
    return this.keycloakService.login({action: "UPDATE_PASSWORD"})
  }

  isLoggedIn(): boolean {
    return this.keycloakService.isLoggedIn();
  }

  logout(): void {
    sessionStorage.removeItem('currentUser');
    this.keycloakService.logout("http://localhost:4200/login");
  }

  hasAuthority(roles:string[]) : boolean {
    return roles.some(role =>this.keycloakService.getKeycloakInstance().hasRealmRole(role));
  }

  getCurrentUser(): any {
    const currentUser = sessionStorage.getItem('currentUser');
    return currentUser ? JSON.parse(currentUser) : null;
  }

  async getUsername(): Promise<string> {
    this.keycloakService.getKeycloakInstance().loadUserInfo().then(userInfo => {
      console.log('User Info:', userInfo);
    }).catch(error => {
      console.error('Failed to load user info', error);
    });
    return "test";
  }
}

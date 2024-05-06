import {KeycloakService} from "keycloak-angular";

import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})

export class AuthorizationService {

  constructor(private readonly keycloakService: KeycloakService) {
  }

  redirectToLoginPage(): void {
    this.keycloakService.login().then();
  }
  userName(): any {
    return this.keycloakService.getKeycloakInstance().tokenParsed;

/*      return this.keycloakService.getKeycloakInstance().loadUserInfo().then(
        userInfo => {
          return userInfo
        })
        .catch(error => {
          return "error"
        });*/
  }

  get token(): Promise<string> {
    return this.keycloakService.getToken();
  }
  isLoggedIn(): boolean {
    return this.keycloakService.isLoggedIn();
  }
  logout(): void {
    this.keycloakService.logout("http://localhost:4200");
  }
}

import {KeycloakService} from "keycloak-angular";
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
    return this.keycloakService.login({redirectUri: "http://localhost:4200/schedule",});
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

  hasAuthority(roles: string[]): boolean {
    return roles.some(role => this.keycloakService.getKeycloakInstance().hasRealmRole(role));
  }



  getUserName(): string {
    const tokenParsed = this.keycloakService.getKeycloakInstance().tokenParsed;
    return tokenParsed ? tokenParsed['preferred_username'] : '';
  }

}

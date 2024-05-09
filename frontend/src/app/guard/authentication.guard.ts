import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from "@angular/router";
import {inject, Injectable} from "@angular/core";
import {KeycloakAuthGuard, KeycloakService} from "keycloak-angular";


@Injectable({providedIn: 'root'})
export class AuthenticationGuard extends KeycloakAuthGuard {

  constructor(override readonly router: Router, protected readonly keycloak: KeycloakService)
  {
    super(router, keycloak);
  }

  public async isAccessAllowed (route: ActivatedRouteSnapshot, state: RouterStateSnapshot){

    if (!this.authenticated) {
      console.log("State:" + state.url)
        await this.keycloak.login();
    }
    return true;
  }

  public isAuthenticated(): boolean {
    return this.authenticated;
  }

}

// Guard checks whether user is logged in
export const AuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot)=> {
  return inject(AuthenticationGuard).canActivate(next, state);
}


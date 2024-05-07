import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from "@angular/router";
import {inject, Injectable} from "@angular/core";
import {AuthenticationService} from "../services/authentication/authentication.service";
import {KeycloakAuthGuard, KeycloakService} from "keycloak-angular";

@Injectable({providedIn: 'root'})
export class AuthenticationGuard extends KeycloakAuthGuard {

  constructor(override readonly router: Router, protected readonly keycloak: KeycloakService, private authenticationService: AuthenticationService,)
  {
    super(router, keycloak);
  }

  public async isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot){

    if (!this.authenticated) {
      await this.keycloak.login({
        redirectUri: window.location.origin + state.url
      });
    }
    //todo: if not authenticated, redirect to login page
    console.log('route-auth-guard', route)
    console.log('state-auth-guard', state)

   if ( this.authenticationService.isAuthenticated()) {
     console.log('authenticated-auth-guard', this.authenticationService.isAuthenticated())
     if (this.authenticationService.hasAuthority('employer') || this.authenticationService.hasAuthority('employee')) {
       return true // user has the required authorities
     } else {
       console.log(`required role not granted on path ${route.pathFromRoot}: ${route.data['requiredAuth']}`)
       return false
     }
   }
    void this.router.navigate(['login']);
    //else is authenticated
    return false ;
  }
}

export const AuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot)=> {
  return inject(AuthenticationGuard).canActivate(next, state);
}

import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot,} from "@angular/router";
import {inject, Injectable} from "@angular/core";
import {AuthenticationService} from "../services/authentication/authentication.service";

@Injectable({providedIn: 'root'})
export class AuthenticationGuard {

  constructor(private router: Router,
              private authenticationService: AuthenticationService,) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
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
    return false  ;

  }
}

export const AuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  return inject(AuthenticationGuard).canActivate(next, state);
}

import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot,} from "@angular/router";
import {inject, Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class AuthenticationGuard {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    //todo: if not authenticated, redirect to login page
    console.log('route-auth-guard', route)
    console.log('state-auth-guard', state)

/*   if ( this.authenticationService.isAuthenticated()) {
     if (this.authenticationService.hasAnyAuthority(route.data['requiredAuth'])) {
       return true // user has the required authorities
     } else {
       console.log(`required role not granted on path ${route.pathFromRoot}: ${route.data['requiredAuth']}`)
       return false
     }
   }*/

    //else is authenticated
    return true;

  }
}

export const AuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  return inject(AuthenticationGuard).canActivate(next, state);
}

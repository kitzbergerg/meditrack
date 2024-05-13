import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from "@angular/router";
import {inject, Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class ShellRedirectionGuard {

  constructor(private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    console.log('route-sr', route)
    console.log('state-sr', state)

    const isEmployer = true //todo
    const isEmployee = true


    //if (/*authenticationService.*/isEmployer/*()*/) {
     // this.router.navigate(['department-manager-dashboard']).catch();
    //} else if (isEmployee) {
    //  this.router.navigate(['employee-dashboard']).catch();
    //}
    
    return true;
  }
}
export const ShellRedirectGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  return inject(ShellRedirectionGuard).canActivate(next, state);
}

import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from '@angular/router';
import {inject} from "@angular/core";
import {AuthorizationService} from "../services/authentication/authorization.service";

export const employeeGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthorizationService);
  const router:Router = inject(Router);
  if (authService.hasAuthority(["admin", "dm", "employee"])) {
    return true;
  } else {
    return false;
  }
};

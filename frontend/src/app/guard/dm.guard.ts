import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthorizationService} from "../services/authorization/authorization.service";

export const dmGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthorizationService);
  const router:Router = inject(Router);

  if (authService.hasAuthority(["admin", "dm"])) {
    return true;
  } else {
    router.navigate(["/schedule"]).then();
    return false;
  }
};

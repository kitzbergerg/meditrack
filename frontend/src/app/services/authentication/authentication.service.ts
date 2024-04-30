import {Injectable} from "@angular/core";
import {Observable, of, throwError} from "rxjs";
import {UserAuthInterface} from "../../interfaces/roles/userAuthInterface";
//import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy'

//@UntilDestroy()
@Injectable({providedIn: 'root'})
export class AuthenticationService {
  userAuth: UserAuthInterface | null = null


  isAuthenticated(): boolean {
    return this.userAuth != null;
  }

  login(stub: string): Observable<string> {
    //return this.backend.login(stub);


    if (stub === "employer") {
      this.userAuth?.roles.push(stub)
      this.userAuth = {
        id: 1,
        username: 'a',
        roles: ['employer']
      }
      return of('employer')
    } else if (stub === "employee") {
      if (this.userAuth == null) {
        this.userAuth = {
          id: 1,
          username: 'a',
          roles: ['employee']
        }
      }
      this.userAuth?.roles.push(stub)
      return of('employee')
    }
    return throwError('a')
  }

  hasAuthority(authority: string) {
    console.log( authority );
    console.log(this.userAuth?.roles)

    if (!authority || !this.isAuthenticated() || !this.userAuth?.roles) {
      return false
    }
    return this.userAuth?.roles.includes(authority);
  }
}

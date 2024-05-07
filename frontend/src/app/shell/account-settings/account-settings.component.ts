import {Component} from '@angular/core';
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-account-settings',
  templateUrl: './account-settings.component.html',
  styleUrls: ['./account-settings.component.scss']
})
export class AccountSettingsComponent {

  workgroupName = "Workgroup Name"

  isEmployer = false;
  isEmployee = false;
  routerRedirect = ''

  constructor(private authenticationService: AuthenticationService,
              private route: ActivatedRoute,
              private router: Router) {
    this.workgroupName = "workgroupName todo";
    if (authenticationService.isAuthenticated()) {
      if (authenticationService.hasAuthority('employer')) {
        this.isEmployer = true;
      } else if (authenticationService.hasAuthority('employee')) {
        this.isEmployee = true;
      }
    }

    this.route.queryParams.subscribe(params => {
      this.routerRedirect = params['from'];
    });
  }

  routeBasedOnRole() {
    return '..' + this.routerRedirect
  }
}

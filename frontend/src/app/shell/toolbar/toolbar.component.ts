import {Component} from '@angular/core';
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent {
  workgroupName = "Workgroup Name"

  constructor(private authenticationService: AuthenticationService,
              private router: Router) {
    this.workgroupName = "workgroupName todo";
  }

  isEmployer() {
    if (this.authenticationService.isAuthenticated()) {
      return this.authenticationService.hasAuthority('employer')
    }
    return false;
  }

  isEmployee() {
    if (this.authenticationService.isAuthenticated()) {
      return this.authenticationService.hasAuthority('employee');
    }
    return false;
  }

  getDashboard() {
    if (this.isEmployer()) {
      return 'department-manager-dashboard'
    } else if (this.isEmployee()) {
      return 'employee-dashboard'
    }
    return ''
  }

  getCurrentRoute() {
    return this.router.url
  }
}

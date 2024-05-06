import { Component } from '@angular/core';
import {AuthenticationService} from "../../services/authentication/authentication.service";
@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent {
  workgroupName = "Workgroup Name"
  isEmployer = false;
  isEmployee = false;

  constructor(authenticationService: AuthenticationService,) {
    this.workgroupName = "workgroupName todo";
    if (authenticationService.isAuthenticated()) {
      if (authenticationService.hasAuthority('employer')) {
        this.isEmployer = true;
      } else if(authenticationService.hasAuthority('employee')) {
        this.isEmployee = true;
      }
    }
  }

  getDashboard() {
    if (this.isEmployer) {
      return 'department-manager-dashboard'
    } else if (this.isEmployee) {
      return 'employee-dashboard'
    }
    return ''
  }
}

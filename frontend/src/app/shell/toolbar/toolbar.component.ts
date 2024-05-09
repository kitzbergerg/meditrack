import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {AuthorizationService} from "../../services/authentication/authorization.service";


@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent {
  workgroupName = "Workgroup Name"
  isEmployee = false;
  isDM = false;

  constructor(private router: Router, private authorizationService: AuthorizationService ) {
    this.workgroupName = "workgroupName todo";

    this.isEmployee = this.authorizationService.hasAuthority(["employee"]);
    this.isDM = this.authorizationService.hasAuthority(["admin", "dm"]);
  }

  getDashboard() {
    return 'dashboard'
  }

  getCurrentRoute() {
    return this.router.url
  }

  logout() {
    this.authorizationService.logout();
  }
}

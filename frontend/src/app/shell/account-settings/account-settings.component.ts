import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../services/user.service";


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
  data: any;
  userId = '';

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()
  }

  constructor(private authorizationService: AuthorizationService, private http: HttpClient, private userService: UserService, private route: ActivatedRoute, private router: Router) {
    this.workgroupName = "workgroupName todo";

    this.isEmployer = true;
    this.isEmployee = true;

    this.route.queryParams.subscribe(params => {
      this.routerRedirect = params['from'];
    });
  }

  routeBasedOnRole() {
    return '..' + this.routerRedirect
  }

  logout() {
    this.authorizationService.logout();
  }

  changePassword() {
    this.authorizationService.changePassword().then();
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe(
      (response) => {
        this.data = response;
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );
  }
}

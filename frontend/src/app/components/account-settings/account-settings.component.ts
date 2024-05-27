import {Component} from '@angular/core';
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";


@Component({
  selector: 'app-account-settings',
  templateUrl: './account-settings.component.html',
  styleUrls: ['./account-settings.component.scss']
})
export class AccountSettingsComponent {

  data: User = {
    canWorkShiftTypes: [],
    currentOverTime: undefined,
    email: "",
    firstName: "",
    holidays: [],
    id: "",
    lastName: "",
    password: "",
    preferences: "",
    preferredShiftTypes: [],
    requestedShiftSwaps: [],
    role: { name: "", color: "", abbreviation: ""},
    roles: [],
    shifts: [],
    specialSkills: [],
    suggestedShiftSwaps: [],
    team: "",
    username: "",
    workingHoursPercentage: 0
  };
  userId = '';

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()
  }

  constructor(private authorizationService: AuthorizationService, private http: HttpClient, private userService: UserService) {
  }

  logout() {
    this.authorizationService.logout();
  }

  changePassword() {
    this.authorizationService.changePassword().then();
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe( {
      next: (user) => {
        this.data = user;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }}
    );
  }
}

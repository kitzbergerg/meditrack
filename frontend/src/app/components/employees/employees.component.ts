import { Component } from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";
import {TeamService} from "../../services/team.service";
import {Team} from "../../interfaces/team";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.scss']
})
export class EmployeesComponent {

  user: User = {
    id: '',
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: '',
    roles: [],
    workingHoursPercentage: 0,
    currentOverTime: 0,
    specialSkills: [],
    holidays: [],
    shifts: [],
    requestedShiftSwaps: [],
    suggestedShiftSwaps: [],
    canWorkShiftTypes: [],
    preferredShiftTypes: []
  };

  userId = '';
  newTeam: Team = { name: '' };
  team: Team = {
    name: '',
  }

  constructor(private authorizationService: AuthorizationService,
              private userService: UserService,
              private teamService: TeamService
  ) {
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()
  }

  createTeam() {
    console.log("Creating Team");
    this.teamService.createTeam(this.newTeam).subscribe(
      (response) => {
        this.team = response;
        console.log(response)
      },
      (error) => {
        console.error('Error fetching team:', error);
      }
    );
  }

  isTeamNameSet(): boolean {
    const b = !this.newTeam.name.trim();
    return b;
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe(
      (response) => {
        this.user = response;
        console.log(response)
        this.getTeam()
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );
  }

  getTeam(): void {
    if (this.user.team !== undefined) {
      this.teamService.getTeamById(this.user.team).subscribe(
        (response) => {
          this.team = response;
          console.log(response)
        },
        (error) => {
          console.error('Error fetching data:', error);
        }
      );
    }
  }
}

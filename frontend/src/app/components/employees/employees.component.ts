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
  newUser: User = {
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

  submitted: boolean = false;
  showNewUserForm: boolean = false;
  employeeForm: UntypedFormGroup;

  userId = '';
  newTeam: Team = { name: '' };
  team: Team = {
    name: '',
  }

  constructor(private authorizationService: AuthorizationService,
              private userService: UserService,
              private teamService: TeamService,
              private formBuilder: UntypedFormBuilder
  ) {
    this.employeeForm = this.formBuilder.group(
      {
        username: [
          '',
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(20)
          ]
        ],
        password: ['', [Validators.required, Validators.minLength(8)]],
        email: [
          '',
          [
            Validators.required,
            Validators.pattern(
              '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'
            )
          ]
        ],
        firstName: [
          '',
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(30),
            Validators.pattern("^([A-Za-züöäÜÖÄ'-]+(\\s[A-Za-züöäÜÖÄ'-]+)*)$")
          ]
        ],
        lastName: [
          '',
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(30),
            Validators.pattern("^^([A-Za-züöäÜÖÄ'-]+(\\s[A-Za-züöäÜÖÄ'-]+)*)$")
          ]
        ],
        workingHoursPercentage: [
          0,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(100)
          ]
        ]
      }
    );
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()
    this.resetForm();
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

  createUser() {
    this.submitted = true;
    if (this.employeeForm.valid) {
      console.log('Creating employee');
      this.newUser = this.employeeForm.value;
      this.userService.createUser(this.newUser)
        .subscribe(
        (response) => {
          console.log('User created successfully:', response);
          this.resetForm();
          this.newUser = this.employeeForm.value;
          this.showNewUserForm = false;
          this.submitted = true;
        },
        (error) => {
          console.error('Error creating user:', error);
        }
      );
    }
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

  toggleNewUserForm() {
    this.showNewUserForm = !this.showNewUserForm;
    this.resetForm();
  }

  resetForm() {
    this.employeeForm = this.formBuilder.group({
      id: [null],
      username: ['', Validators.required],
      password: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      roles: [[]],
      role: [null],
      workingHoursPercentage: [0, [Validators.required, Validators.min(0)]],
      currentOverTime: [null],
      specialSkills: [[]],
      team: [null],
      holidays: [[]],
      preferences: [null],
      requestedShiftSwaps: [[]],
      suggestedShiftSwaps: [[]],
      shifts: [[]],
      canWorkShiftTypes: [[]],
      preferredShiftTypes: [[]]
    });
  }
}

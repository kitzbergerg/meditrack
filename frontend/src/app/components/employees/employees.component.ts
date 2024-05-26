import { Component } from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";
import {TeamService} from "../../services/team.service";
import {Team} from "../../interfaces/team";
import {Table} from "primeng/table";
import {RolesService} from "../../services/roles.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ShiftService} from "../../services/shift.service";
import {ShiftType} from "../../interfaces/shiftType";

@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.scss']
})
export class EmployeesComponent {

  userDialog = false;
  teamComponentHeader = "employees";
  userHeader = "";
  deleteUserDialog= false;
  submitted = false;
  newUserForm: FormGroup;

  newUser: User = {
    username: "",
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
    workingHoursPercentage: 1
  };

  selectedUsers: User[] = [];

  roles: any[] = [];
  shiftTypes: any[] = [];

  currentUser: User = {
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
    role: { name: "", color: "", abbreviation: ""},
    team: undefined,
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
  usersFromTeam: User[] =[];
  cols: any[] = [];


  constructor(private authorizationService: AuthorizationService,
              private userService: UserService,
              private teamService: TeamService,
              private rolesService: RolesService,
              private formBuilder: FormBuilder,
              private shiftService: ShiftService,
  ) {
    this.newUserForm = this.formBuilder.group({
      username: ['', this.usernameValidator.bind(this)],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      workingHoursPercentage: [1, [Validators.required, Validators.min(0.1), Validators.max(1.0)]],
      role: [null, Validators.required],
      canWorkShiftTypes: [[]]
    });
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()

    this.cols = [
      { field: 'username', header: 'Name' },
      { field: 'firstName', header: 'First Name' },
      { field: 'lastName', header: 'Last Name' },
      { field: 'email', header: 'Email' },
      { field: 'role', header: 'Role' },
      { field: 'workingHoursPercentage', header: 'WorkingHoursPercentage' },
      { field: 'canWorkShiftTypes', header: 'CanWorkShiftTypes' },
    ];
  }

  receiveTeam(team: Team) {
    this.currentUser.team = team.id;
    this.team = team;
  }

  usernameValidator(control: any): { [key: string]: any } | null {
    if (this.userHeader === "Edit User" && control.value === null) {
      return null;
    }
    return Validators.required(control);
  }

  loadRoles(): void {
    this.rolesService.getAllRoles()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
      });
  }
  loadShiftTypes() {
    this.shiftService.getAllShiftTypes().subscribe({
      next: (response) => {
        this.shiftTypes = response;
        console.log(this.shiftTypes)
        console.log("Fetched Shift Types successfully")
      },
      error: (error) => {
        console.error('Error fetching shift types:', error)
      },
    });
    this.resetUser()
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe(
      (response) => {
        this.currentUser = response;
        if (response.team != null) {
          this.getTeam();
          this.loadUsersFromTeam()
          this.loadRoles()
          this.loadShiftTypes()
        }
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );
  }

  getTeam(): void {
    if (this.currentUser.team !== undefined ) {
      this.teamService.getTeamById(this.currentUser.team).subscribe(
        (response) => {
          this.team = response;
        },
        (error) => {
          console.error('Error fetching data:', error);
        }
      );
    }
  }

  loadUsersFromTeam(): void {
      this.userService.getAllUserFromTeam()
        .subscribe(users => {
          this.usersFromTeam = users.filter(user => user.id !== this.currentUser.id)
        });
  }

  openNew() {
    this.resetUser();
    this.submitted = false;
    this.userHeader = "Create User"
    this.userDialog = true;
  }

  editUser(user: User) {
    const selectedRole = this.roles.find(role => role.id === user.role.id);
    const userShiftTypeIds = user.canWorkShiftTypes.map(shiftType => shiftType.id);
    const selectedShiftTypes = this.shiftTypes.filter(shiftType => userShiftTypeIds.includes(shiftType.id));

    this.newUserForm.patchValue({
      username: null,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      workingHoursPercentage: user.workingHoursPercentage,
      role: selectedRole,
      canWorkShiftTypes: selectedShiftTypes,
    });
    this.newUser = { ...user };
    this.userHeader = "Edit User";
    this.userDialog = true;
  }

  deleteUser(user: User) {
    this.deleteUserDialog = true;
    this.newUser = { ...user };
  }

  confirmDelete() {
    this.deleteUserDialog = false;
    this.usersFromTeam = this.usersFromTeam.filter(val => val.id !== this.newUser.id);
    this.userService.deleteUser(this.newUser).subscribe({
        next: () => {console.log("User deleted successfully: ", this.newUser)},
        error: () => {console.log("User could not be deleted: ", this.newUser)}}
    );
    this.resetUser()
  }

  hideDialog() {
    this.userDialog = false;
    this.submitted = false;
  }

  createUser() {
    this.submitted = true;

    if (!this.newUserForm.invalid) {
        if (this.newUser.id) {
          this.newUser = { ...this.newUser, ...this.newUserForm.value };
          this.userService.updateUser(this.newUser).subscribe({
            next: (user) => {
              console.log("Successfully updated user", user);
              if(user.id) {
                this.usersFromTeam[this.findIndexById(user.id)] = user;
              }
              this.userDialog = false;
            },
            error: () => {
              console.log("Error updating user", this.newUser)
            }
          })
        }else {
          this.newUser = this.newUserForm.value;
          this.newUser.roles = ['employee'];
          this.newUser.team = this.team.id;
          this.newUser.password = <string>this.newUser.username;
          this.userService.createUser(this.newUser)
            .subscribe({
              next: (response) => {
                this.userDialog = false;
                console.log('User created successfully:', response);
                this.usersFromTeam = [...this.usersFromTeam];
                this.usersFromTeam.push(response);
                this.resetUser()
              },
              error: (error) => {
                console.error('Error creating user:', error);
                //this.resetUser()
              }}
            );
      }
    }
  }

  findIndexById(id: string): number {
    let index = -1;
    for (let i = 0; i < this.usersFromTeam.length; i++) {
      if (this.usersFromTeam[i].id === id) {
        index = i;
        break;
      }
    }
    return index;
  }

  resetUser() {
    this.newUser = {
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
      workingHoursPercentage: 1
    };
    this.newUserForm.reset({workingHoursPercentage: 1});
  }

  onGlobalFilter(table: Table, event: Event) {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }
}

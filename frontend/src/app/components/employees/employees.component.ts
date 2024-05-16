import { Component } from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";
import {TeamService} from "../../services/team.service";
import {Team} from "../../interfaces/team";
import {Table} from "primeng/table";
import {RolesService} from "../../services/roles.service";

@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.scss']
})
export class EmployeesComponent {

  userDialog = false;
  userHeader = "Create User";

  deleteUserDialog= false;

  deleteUsersDialog = false;
  submitted = false;

  newUser: User = {
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
    role: "",
    roles: [],
    shifts: [],
    specialSkills: [],
    suggestedShiftSwaps: [],
    team: "",
    username: "",
    workingHoursPercentage: 0
  };

  selectedUsers: User[] = [];


  roles: any[] = [];

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
  ) {
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()
    this.loadRoles()

    this.cols = [
      { field: 'username', header: 'Name' },
      { field: 'firstName', header: 'First Name' },
      { field: 'lastName', header: 'Last Name' },
      { field: 'email', header: 'Email' },
      { field: 'roles', header: 'Role' },
      { field: 'workingHoursPercentage', header: 'WorkingHoursPercentage' },
    ];

  }

  loadRoles(): void {
    this.rolesService.getAllRoles()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
      });
  }

  createTeam() {
    console.log("Creating Team");
    this.teamService.createTeam(this.newTeam).subscribe(
      (response) => {
        this.user.team= response.id;
        this.team = response;
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
        if (response.team != null) {
          this.getTeam();
          this.loadUsersFromTeam()
        }
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );
  }

  getTeam(): void {
    console.log(this.user)
    if (this.user.team !== undefined ) {
      this.teamService.getTeamById(this.user.team).subscribe(
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
          this.usersFromTeam = users.filter(user => user.id !== this.user.id)
        });
  }


  openNew() {
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
      role: "",
      roles: [],
      shifts: [],
      specialSkills: [],
      suggestedShiftSwaps: [],
      team: "",
      username: "",
      workingHoursPercentage: 1.0
    };
    this.submitted = false;
    this.userHeader = "Create User"
    this.userDialog = true;
  }

  deleteSelectedUsers() {
    this.deleteUsersDialog = true;
  }

  editUser(user: User) {
    this.newUser = { ...user };
    this.userHeader = "Edit User"
    this.userDialog = true;
  }

  deleteUser(user: User) {
    this.deleteUserDialog = true;
    this.newUser = { ...user };
  }

  confirmDeleteSelected() {
    this.deleteUsersDialog = false;
    this.usersFromTeam = this.usersFromTeam.filter(val => !this.selectedUsers.includes(val));
    // delete TODO
    this.selectedUsers = [];
  }

  confirmDelete() {
    this.deleteUserDialog = false;
    this.usersFromTeam = this.usersFromTeam.filter(val => val.id !== this.newUser.id);
    // delete TODO
    this.resetUser()
  }

  hideDialog() {
    this.userDialog = false;
    this.submitted = false;
  }


  createUser() {
    this.submitted = true;
    //if (valid) { // Valid input TODO
      if (this.newUser.username?.trim()) {
        if (this.newUser.id) {
          this.usersFromTeam[this.findIndexById(this.newUser.id)] = this.newUser;
          // Update User TODO
        }else {
          console.log('Creating employee');
          this.newUser.roles = ['employee']
          this.newUser.team = this.team.id
          this.usersFromTeam = [...this.usersFromTeam];
          this.userDialog = false;
          this.userService.createUser(this.newUser)
            .subscribe(
              (response) => {
                console.log('User created successfully:', response);
                this.usersFromTeam.push(response);
                this.resetUser()
              },
              (error) => {
                console.error('Error creating user:', error);
              }
            );

        }
      }
    //}
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
      role: "",
      roles: [],
      shifts: [],
      specialSkills: [],
      suggestedShiftSwaps: [],
      team: "",
      username: "",
      workingHoursPercentage: 0
    };
  }

  onGlobalFilter(table: Table, event: Event) {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }
}

import {Component} from '@angular/core';
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {UserService} from "../../services/user.service";
import {User} from "../../interfaces/user";
import {TeamService} from "../../services/team.service";
import {Team} from "../../interfaces/team";
import {Table} from "primeng/table";
import {RolesService} from "../../services/roles.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {FilterService, MessageService} from "primeng/api";
import {ShiftTypeService} from "../../services/shift-type.service";
import {ShiftType} from "../../interfaces/shiftType";
import {Role} from "../../interfaces/role";
import {ShiftService} from "../../services/shift.service";

@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.scss']
})
export class EmployeesComponent {

  loading = true;

  userDialog = false;
  teamComponentHeader = "employees";
  userHeader = "";
  deleteUserDialog = false;
  submitted = false;
  newUserForm: FormGroup;

  newUser: User = {
    username: "",
    canWorkShiftTypes: [],
    preferredShiftTypes: [],
    currentOverTime: undefined,
    email: "",
    firstName: "",
    holidays: [],
    id: "",
    lastName: "",
    password: "",
    preferences: "",
    requestedShiftSwaps: [],
    role: {name: "", color: "", abbreviation: ""},
    roles: [],
    shifts: [],
    suggestedShiftSwaps: [],
    team: "",
    workingHoursPercentage: 100
  };

  selectedUsers: User[] = [];

  roles: Role[] = [];
  shiftTypes: ShiftType[] = [];

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
    holidays: [],
    shifts: [],
    role: {name: "", color: "", abbreviation: ""},
    team: undefined,
    requestedShiftSwaps: [],
    suggestedShiftSwaps: [],
    canWorkShiftTypes: [],
    preferredShiftTypes: []
  };

  userId = '';
  newTeam: Team = {name: ''};
  team: Team = {
    name: '',
  }
  usersFromTeam: User[] = [];
  cols: any[] = [];


  constructor(private authorizationService: AuthorizationService,
              private userService: UserService,
              private teamService: TeamService,
              private rolesService: RolesService,
              private formBuilder: FormBuilder,
              private shiftService: ShiftService,
              private shiftTypeService: ShiftTypeService,
              private messageService: MessageService,
              private filterService: FilterService
  ) {
    this.newUserForm = this.formBuilder.group({
      username: ['', this.usernameValidator.bind(this)],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      workingHoursPercentage: [100, [Validators.required, Validators.min(1), Validators.max(100)]],
      role: [null, Validators.required],
      canWorkShiftTypes: [[]],
      preferredShiftTypes: [[]]
    });

    this.filterService.register('customFilter', (value: any[], filter: string): boolean => {
      if (!filter || !value) {
        return true;
      }
      return value.some(shiftType => shiftType.abbreviation === filter);
    });
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser()

    this.cols = [
      {field: 'username', header: 'Name'},
      {field: 'firstName', header: 'First Name'},
      {field: 'lastName', header: 'Last Name'},
      {field: 'email', header: 'Email'},
      {field: 'role', header: 'Role'},
      {field: 'workingHoursPercentage', header: 'WorkingHoursPercentage'},
      {field: 'canWorkShiftTypes', header: 'CanWorkShiftTypes'},
      {field: 'preferredShiftTypes', header: 'PreferredShiftTypes'},
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
    this.rolesService.getAllRolesFromTeam()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
      });
  }

  loadShiftTypes() {
    this.shiftTypeService.getAllShiftTypesByTeam().subscribe({
      next: (response: ShiftType[]) => {
        this.shiftTypes = response;
      },
      error: (error: { error: any; }) => {
        this.messageService.add({severity: 'error', summary: 'Error fetching Shift Types: ', detail: error.error});
      },
    });
    this.resetUser()
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe({
      next:
        (response) => {
          this.currentUser = response;
          if (response.team != null) {
            this.getTeam();
            this.loadUsersFromTeam()
            this.loadRoles()
            this.loadShiftTypes()
          }
        },
      error: (error) => {
        this.messageService.add({severity: 'error', summary: 'Error Fetching User: ', detail: error.error});
      }
    });
  }

  getTeam(): void {
    if (this.currentUser.team !== undefined) {
      this.teamService.getTeamById(this.currentUser.team).subscribe({
        next:
          (team) => {
            this.team = team;
          },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Error Fetching team: ', detail: error.error});
        }
      });
    }
  }

  loadUsersFromTeam(): void {
    this.userService.getAllUserFromTeam()
      .subscribe(users => {
        this.usersFromTeam = users;
        this.loading = false;
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
    const userSelectedShiftTypesIds = user.preferredShiftTypes.map(shiftType => shiftType.id);
    const selectedShiftTypes = this.shiftTypes.filter(shiftType => userShiftTypeIds.includes(shiftType.id));
    const preferredSelectedShiftTypes = this.shiftTypes.filter(shiftType => userSelectedShiftTypesIds.includes(shiftType.id));

    this.newUserForm.patchValue({
      username: null,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      workingHoursPercentage: user.workingHoursPercentage,
      role: selectedRole,
      canWorkShiftTypes: selectedShiftTypes,
      preferredShiftTypes: preferredSelectedShiftTypes,
    });
    this.newUser = {...user};
    this.userHeader = "Edit User";
    this.userDialog = true;
  }

  deleteUser(user: User) {
    this.deleteUserDialog = true;
    this.newUser = {...user};
  }

  confirmDelete() {
    this.deleteUserDialog = false;
    this.usersFromTeam = this.usersFromTeam.filter(val => val.id !== this.newUser.id);
    this.userService.deleteUser(this.newUser).subscribe({
      next: () => {
        this.messageService.add({severity: 'success', summary: 'Successfully Deleted User ' + this.newUser.firstName});
      },
      error: (error) => {
        this.messageService.add({severity: 'error', summary: 'Deleting User Failed', detail: error.error});
      }
    });
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
        this.newUser = {...this.newUser, ...this.newUserForm.value};
        this.userService.updateUser(this.newUser).subscribe({
          next: (user) => {
            if (user.id) {
              //this.usersFromTeam[this.findIndexById(user.id)] = user;
              this.usersFromTeam = this.usersFromTeam.filter(u => u.id != user.id)
              this.usersFromTeam.push(user);
            }
            this.messageService.add({severity: 'success', summary: 'Successfully Updated User ' + user.firstName});
            this.userDialog = false;
          },
          error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Updating User Failed'});
          }
        })
      } else {
        this.newUser = this.newUserForm.value;
        if (this.currentUser.roles.includes("admin")) {
          this.newUser.roles = ['dm'];
        } else {
          this.newUser.roles = ['employee'];
        }
        this.newUser.team = this.team.id;
        this.newUser.password = <string>this.newUser.username;
        this.userService.createUser(this.newUser)
          .subscribe({
              next: (user) => {
                this.userDialog = false;
                this.usersFromTeam = [...this.usersFromTeam];
                this.usersFromTeam.push(user);
                this.resetUser()
                this.messageService.add({severity: 'success', summary: 'Successfully Created User ' + user.firstName});
              },
              error: (error) => {
                this.messageService.add({severity: 'error', summary: 'Creating User Failed ' + error.error});
              }
            }
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
      preferredShiftTypes: [],
      currentOverTime: undefined,
      email: "",
      firstName: "",
      holidays: [],
      id: "",
      lastName: "",
      password: "",
      preferences: "",
      requestedShiftSwaps: [],
      role: {name: "", color: "", abbreviation: ""},
      roles: [],
      shifts: [],
      suggestedShiftSwaps: [],
      team: "",
      username: "",
      workingHoursPercentage: 100
    };
    this.newUserForm.reset({workingHoursPercentage: 100});
  }

  onGlobalFilter(table: Table, event: Event) {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }
}

import {ChangeDetectorRef, Component} from '@angular/core';
import {RolesService} from "../../services/roles.service";
import {Role} from "../../interfaces/role";
import {User} from "../../interfaces/user";
import {UserService} from "../../services/user.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {MessageService} from "primeng/api";
import {Team} from "../../interfaces/team";

@Component({
  selector: 'app-roles',
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.scss'
})
export class RolesComponent {

  loading = true;
  teamComponentHeader = "roles";
  roles: Role[] = [];
  selectedRole: Role | undefined;
  role: Role = { id: 0, name: '', color: '#ff0000', abbreviation: ''};
  userId = '';

  submitted = false;
  valid = false;

  initialLoad= false;

  formTitle= '';
  formAction= '';
  formMode: 'create' | 'edit' | 'details' = 'details';

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
    role: {name: "", color: "", abbreviation: ""},
    team: undefined,
    requestedShiftSwaps: [],
    suggestedShiftSwaps: [],
    canWorkShiftTypes: [],
    preferredShiftTypes: []
  };

  constructor(private rolesService: RolesService,
              private  userService: UserService,
              private authorizationService: AuthorizationService,
              private cdr: ChangeDetectorRef,
              private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser();
  }

  receiveTeam(team: Team) {
    this.currentUser.team = team.id;
    this.loadRoles()
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe(
      (response) => {
        this.currentUser = response;
        if (response.team != null) {
          this.loadRoles()
        }
        this.loading = false;
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );
  }

  loadRoles(): void {
    this.rolesService.getAllRolesFromTeam()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
        if (this.roles.length === 0) {
          this.formMode = 'create';
        }
        if (this.roles.length > 0 && !this.initialLoad) {
          this.initialLoad = true;
          this.selectRole(this.roles[0]);
        }
      });
  }

  deleteRole(): void {
    if (this.role.id != undefined) {
      this.rolesService.deleteRole(this.role.id)
        .subscribe({
          next: (response) => {
          console.log('Role deleted successfully:', response);
          this.messageService.add({severity:'success', summary: 'Successfully Deleted Role ' + this.role.name});
          this.loadRoles();
          this.resetForm();
        }, error: (error) => {
          console.error('Error deleting role:', error);
          this.messageService.add({severity:'error', summary: 'Deleting Role Failed'});
        }});
    }
  }

  getRole(id: number) {
    this.rolesService.getRole(id)
      .subscribe((response: Role) => {
        console.log('Role retrieved successfully:', response);
        this.role = response;
        this.loadRoles();
      }, error => {
        console.error('Error retrieving Role:', error);
      });
  }

  createRole() {
    this.submitted = true;

    if (this.valid) {
      const newRole: Role = {
        name: this.role.name,
        color: this.role.color,
        abbreviation: this.role.abbreviation,
        team: this.currentUser.team
      }
      this.rolesService.createRole(newRole)
        .subscribe({
          next: (response) => {
            console.log('Role created successfully:', response);
            this.messageService.add({severity:'success', summary: 'Successfully Created Role ' + newRole.name});
            this.loadRoles();
            this.resetForm();
          }, error: (error) => {
            console.error('Error creating role:', error);
            if (error.error === "data integrity violation") {
              this.messageService.add({severity:'error', summary: 'Creating Role Failed', detail: "Name, Color and Abbreviation have to be unique."});
            } else {
              this.messageService.add({severity:'error', summary: 'Creating Role Failed', detail: error.error});
            }
          }
        });
    } else {
      this.messageService.add({severity:'warn', summary: 'Validation Failed', detail: 'Please read the warnings.'});
    }
  }

  updateRole() {
    this.submitted = true;

    if (this.valid) {
      this.rolesService.updateRole(this.role)
        .subscribe(response => {
          console.log('Role updated successfully:', response);
          this.messageService.add({severity:'success', summary: 'Successfully Updated Role ' + this.role.name});
          this.selectRole(this.role);
          this.resetForm();
        }, error => {
          console.error('Error updating role:', error);
          if (error.error === "data integrity violation") {
            this.messageService.add({severity:'error', summary: 'Updating Role Failed', detail: "Name, Color and Abbreviation have to be unique."});
          } else {
            this.messageService.add({severity:'error', summary: 'Updating Role Failed', detail: error.error});
          }
        });
    } else {
      this.messageService.add({severity:'warn', summary: 'Validation Failed', detail: 'Please read the warnings.'});
    }
  }

  showCreateForm() {
    this.resetForm();
    this.formMode = 'create';
  }

  selectRole(role: Role | undefined) {
    if (role == undefined) {
      return;
    }
    if (role.id != undefined) {
      this.getRole(role.id);
      this.formMode = 'details';
    }
  }

  editRole() {
    this.formMode = 'edit';
  }

  getFormTitle(): string {
    if (this.formMode === 'create') {
      this.formTitle = 'Create Role';
      this.formAction = 'Create';
    } else if (this.formMode === 'edit') {
      this.formTitle = 'Edit Role';
      this.formAction = 'Save';
    } else {
      this.formTitle = 'Role Details';
      this.formAction = 'Edit';
    }
    return this.formTitle;
  }

  createOrUpdateRole() {
    this.valid = (this.role.name !== '') && (this.role.color !== '') && (this.role.abbreviation !== '');
    if (this.formMode === 'create') {
      this.createRole();
    } else if (this.formMode === 'edit') {
      this.updateRole();
      this.getFormTitle();
      this.loadRoles();
    }
  }

  cancelEditing() {
    this.selectRole(this.role);
    this.resetForm();
  }

  onColorChange(event: any) {
    this.role.color = event.value;
    this.cdr.detectChanges();
  }

  resetForm() {
    this.selectedRole = undefined
    this.submitted = false;
    this.role = {id: 0, name: '', color: '#ff0000', abbreviation: '' };
  }
}

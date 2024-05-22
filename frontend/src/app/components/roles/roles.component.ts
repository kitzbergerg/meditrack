import {Component} from '@angular/core';
import {RolesService} from "../../services/roles.service";
import {Role, RoleCreate} from "../../interfaces/roles/rolesInterface";

@Component({
  selector: 'app-roles',
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.scss'
})
export class RolesComponent {

  roles: Role[] = [];
  newRole: RoleCreate = { name: '', color: '', abbreviation: ''};
  currentRole: Role = { id: -1, name: '',  color: '', abbreviation: '' };

  initialLoad= false;

  formTitle= '';
  formAction= '';
  formMode: 'create' | 'edit' | 'details' = 'details';

  constructor(private rolesService: RolesService) { }

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.rolesService.getAllRoles()
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

  deleteRole(id: number): void {
    if (id != null) {
      this.rolesService.deleteRole(id)
        .subscribe({
          next: (response) => {
          console.log('Role deleted successfully:', response);
          this.loadRoles();
            this.resetForm();
        }, error: (error) => {
          console.error('Error deleting role:', error);
        }});
    }
  }

  getRole(id: number) {
    this.rolesService.getRole(id)
      .subscribe((response: Role) => {
        console.log('Role retrieved successfully:', response);
        this.currentRole = response;
        this.loadRoles();
      }, error => {
        console.error('Error retrieving Role:', error);
      });
  }

  createRole() {
    if (this.isRoleNameUnique(this.newRole.name)) {
      this.rolesService.createRole(this.newRole)
        .subscribe({
          next: (response) => {
          console.log('Role created successfully:', response);
          this.loadRoles();
          this.resetForm();
        }, error: (error) => {
          console.error('Error creating role:', error);
      }});
    } else {
      console.error('Role name must be unique.');
    }
  }

  updateRole() {
    const roleToUpdate: Role = {
      id: this.currentRole.id,
      name: this.currentRole.name,
      color: this.currentRole.color,
      abbreviation: this.currentRole.abbreviation
    };
    if (this.isRoleNameUnique(roleToUpdate.name)) {
      this.rolesService.updateRole(roleToUpdate)
        .subscribe(response => {
          console.log('Role updated successfully:', response);
          this.resetForm();
          // update shown shift type and list (case: name was changed)
          this.selectRole(this.currentRole);
        }, error => {
          console.error('Error updating role:', error);
        });
    } else {
      console.error('Role name must be unique.');
    }
  }

  isRoleNameUnique(name: string): boolean {
    return !this.roles.some(role => role.name === name);
  }

  showCreateForm() {
    this.resetForm();
    this.formMode = 'create';
  }

  selectRole(role: Role) {
    this.getRole(role.id);
    this.formMode = 'details';
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
    if (this.formMode === 'create') {
      this.createRole();
    } else if (this.formMode === 'edit') {
      this.updateRole();
      this.getFormTitle();
      this.loadRoles();
    }
  }

  cancelEditing() {
    this.resetForm();
    this.selectRole(this.currentRole);
  }

  resetForm() {
    this.newRole = { name: '', color: '', abbreviation: '' };
  }
}

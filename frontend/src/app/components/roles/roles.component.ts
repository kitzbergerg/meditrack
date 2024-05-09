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
  editedRole: Role = {id: 0, name: '', users: []};
  newRoleName: string = '';
  showNewRoleInputField: boolean = false;

  constructor(private rolesService: RolesService) { }

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.rolesService.getAllRoles()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
      });
  }

  deleteRole(role: Role): void {
    this.rolesService.deleteRole(role.id)
      .subscribe(response => {
        console.log('Role deleted successfully:', response);
        this.loadRoles();
      }, error => {
        console.error('Error deleting role:', error);
      });
  }

  createRole() {
    if (this.isRoleNameUnique(this.newRoleName) && this.newRoleName) {
      const newRole: RoleCreate = {
        name: this.newRoleName
      };
      this.rolesService.createRole(newRole)
        .subscribe(response => {
          console.log('Role created successfully:', response);
          this.loadRoles();
          this.newRoleName = '';
        }, error => {
          console.error('Error creating role:', error);
        });
      this.showNewRoleInputField = false;
    } else {
      console.error('Role name must be unique.');
    }
  }

  startEditing(role: Role) {
    this.editedRole = role;
  }

  updateRole(role: Role) {
    if (this.isRoleNameUnique(role.name)) {
      const roleToUpdate: Role = {
        id: role.id,
        name: role.name,
        users: role.users
      };

      this.rolesService.updateRole(roleToUpdate)
        .subscribe(response => {
          this.editedRole = {id: 0, name: '', users: []};
          console.log('Role updated successfully:', response);
          this.loadRoles();
        }, error => {
          this.editedRole = {id: 0, name: '', users: []};
          console.error('Error updating role:', error);
        });
    } else {
      this.editedRole = {id: 0, name: '', users: []};
      console.error('Role name must be unique.');
    }
  }

  isRoleNameUnique(name: string): boolean {
    return !this.roles.some(role => role.name === name);
  }

  cancelEditing() {
    this.editedRole = {id: 0, name: '', users: []};
  }
}

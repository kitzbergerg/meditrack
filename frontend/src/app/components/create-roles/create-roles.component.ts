import {Component} from '@angular/core';
import {RolesService} from "../../services/roles.service";
import {RoleCreate} from "../../interfaces/roles/rolesInterface";

@Component({
  selector: 'app-create-roles',
  templateUrl: './create-roles.component.html',
  styleUrl: './create-roles.component.scss'
})
export class CreateRolesComponent {

  roleName: string = '';

  constructor(private rolesService: RolesService) {}

  createRole() {
    if (this.roleName) {
      const newRole: RoleCreate = {
        name: this.roleName
      };
      this.rolesService.createRole(newRole)
        .subscribe(response => {
          console.log('Role created successfully:', response);
        }, error => {
          console.error('Error creating role:', error);
        });
    }
  }
}

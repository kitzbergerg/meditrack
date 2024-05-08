import {Component} from '@angular/core';
import {RolesService} from "../../services/roles.service";
import {Role} from "../../interfaces/roles/rolesInterface";

@Component({
  selector: 'app-roles',
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.scss'
})
export class RolesComponent {

  roles: Role[] = [];

  constructor(private rolesService: RolesService) { }

  ngOnInit(): void {
    this.rolesService.getAllRoles()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
      });

    console.log(this.roles);
  }
}

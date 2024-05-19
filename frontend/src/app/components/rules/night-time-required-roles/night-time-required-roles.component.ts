import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Role} from "../../../interfaces/roles/rolesInterface";
import {RolesService} from "../../../services/roles.service";

@Component({
  selector: 'app-night-time-required-roles',
  standalone: true,
  imports: [],
  templateUrl: './night-time-required-roles.component.html',
  styleUrl: './night-time-required-roles.component.scss'
})
export class NightTimeRequiredRolesComponent {
  @Input() nightTimeRequiredRoles: [Role | null, number][] | null = null;
  availableRoles: Role[] = [];
  editMode = false;
  @Output() deleteNightTimeRequiredRoles = new EventEmitter<void>();

  constructor(roleService: RolesService) {
    roleService.getAllRoles().subscribe(x => this.availableRoles = x);
  }

  update() {
    this.editMode = false
    console.log("")
  }

  delete() {
    this.deleteNightTimeRequiredRoles.emit()
  }

  addRole() {
    this.nightTimeRequiredRoles?.push([null, 0])
  }
}

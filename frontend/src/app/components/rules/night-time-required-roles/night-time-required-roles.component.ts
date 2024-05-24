import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Role} from "../../../interfaces/roles/rolesInterface";
import {RolesService} from "../../../services/roles.service";
import {ButtonModule} from "primeng/button";
import {DropdownModule} from "primeng/dropdown";
import {InputNumberModule} from "primeng/inputnumber";
import {NgForOf, NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-night-time-required-roles',
  standalone: true,
  imports: [
    ButtonModule,
    DropdownModule,
    InputNumberModule,
    NgForOf,
    NgIf,
    RippleModule,
    FormsModule
  ],
  templateUrl: './night-time-required-roles.component.html',
  styleUrl: './night-time-required-roles.component.scss'
})
export class NightTimeRequiredRolesComponent {
  @Input() nightTimeRequiredRoles: [Role | null, number][] | null = null;
  availableRoles: Role[] = [];
  editMode = true;
  @Output() updateNightTimeRequiredRoles = new EventEmitter<[Role | null, number][] | null>();

  constructor(roleService: RolesService) {
    roleService.getAllRoles().subscribe(x => this.availableRoles = x);
  }

  update() {
    this.editMode = false
    this.updateNightTimeRequiredRoles.emit(this.nightTimeRequiredRoles)
  }

  delete() {
    this.updateNightTimeRequiredRoles.emit(null)
  }

  addRole() {
    if (this.nightTimeRequiredRoles === undefined || this.nightTimeRequiredRoles === null) {
      this.nightTimeRequiredRoles = [];
      console.log(this.nightTimeRequiredRoles)
    }
    console.log(this.nightTimeRequiredRoles)
    this.nightTimeRequiredRoles?.push([null, 0])
  }

  deleteRoleNumber(roleNumber: [Role | null, number]) {
    this.nightTimeRequiredRoles = this.nightTimeRequiredRoles!.filter(x => x !== roleNumber)
  }
}

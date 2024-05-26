import {Component, EventEmitter, Input, Output} from '@angular/core';
import {RolesService} from "../../../services/roles.service";
import {ButtonModule} from "primeng/button";
import {DropdownModule} from "primeng/dropdown";
import {InputNumberModule} from "primeng/inputnumber";
import {NgForOf, NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {FormsModule} from "@angular/forms";
import {Role} from "../../../interfaces/role";

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
  @Input() nightTimeRequiredRolesInput: object | null = null;
  nightTimeRequiredRoles: [Role | null, number][] | null = null;
  availableRoles: Role[] = [];
  editMode: any;
  @Output() updateNightTimeRequiredRoles = new EventEmitter<[Role | null, number][] | null>();

  constructor(roleService: RolesService) {

    roleService.getAllRoles().subscribe(x => {
      this.availableRoles = x;
      console.log('this.availableRoles', this.availableRoles);
      this.nightTimeRequiredRoles = []
      if (this.nightTimeRequiredRolesInput) {
        for (const [k, v] of Object.entries(this.nightTimeRequiredRolesInput)) {

          // @ts-ignore
          this.nightTimeRequiredRoles.push([this.availableRoles.find(x => x.id == k), v])
        }
        console.log(this.nightTimeRequiredRoles);
      }
      if (this.nightTimeRequiredRolesInput !== null) {
        console.log('false', this.nightTimeRequiredRolesInput);
        this.editMode = false
      } else {
        console.log('true', this.nightTimeRequiredRolesInput);
        this.editMode = true
      }
    })
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

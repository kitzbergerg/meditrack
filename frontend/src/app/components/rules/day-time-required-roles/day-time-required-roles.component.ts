import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ButtonModule} from "primeng/button";
import {InputNumberModule} from "primeng/inputnumber";
import {NgForOf, NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {DropdownModule} from "primeng/dropdown";
import {RolesService} from "../../../services/roles.service";
import {Role} from "../../../interfaces/role";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-day-time-required-roles',
  standalone: true,
  imports: [
    ButtonModule,
    InputNumberModule,
    NgIf,
    RippleModule,
    DropdownModule,
    NgForOf,
    FormsModule
  ],
  templateUrl: './day-time-required-roles.component.html',
  styleUrl: './day-time-required-roles.component.scss'
})
export class DayTimeRequiredRolesComponent {
  @Input() dayTimeRequiredRolesInput: object | null = null;
  dayTimeRequiredRoles: [Role | null, number][] | null = null;
  availableRoles: Role[] = [];
  editMode: any;
  @Output() updateDayTimeRequiredRoles = new EventEmitter<[Role | null, number][] | null>();

  constructor(roleService: RolesService) {
    roleService.getAllRolesFromTeam().subscribe(x => {
      console.log(x)
      this.availableRoles = x
      console.log('this.availableRoles', this.availableRoles);
      this.dayTimeRequiredRoles = []
      if (this.dayTimeRequiredRolesInput) {
        for (const [k, v] of Object.entries(this.dayTimeRequiredRolesInput)) {
          // @ts-ignore
          this.dayTimeRequiredRoles.push([this.availableRoles.find(x => x.id == k), v])
        }
        if (Object.entries(this.dayTimeRequiredRolesInput).length == 0) {
          this.dayTimeRequiredRoles.push([null, 0])
        }
        console.log(this.dayTimeRequiredRoles);
      }
      if (this.dayTimeRequiredRolesInput !== null) {
        this.editMode = false
      } else {
        this.editMode = true
      }
    });
  }

  update() {
    this.editMode = false
    this.updateDayTimeRequiredRoles.emit(this.dayTimeRequiredRoles)
  }

  delete() {
    this.updateDayTimeRequiredRoles.emit(null)
  }

  addRole() {
    if (this.dayTimeRequiredRoles === undefined || this.dayTimeRequiredRoles === null) {
      this.dayTimeRequiredRoles = [];
      console.log(this.dayTimeRequiredRoles)
    }
    console.log(this.dayTimeRequiredRoles)
    this.dayTimeRequiredRoles?.push([null, 0])
  }

  deleteRoleNumber(roleNumber: [Role | null, number]) {
    this.dayTimeRequiredRoles = this.dayTimeRequiredRoles!.filter(x => x !== roleNumber)
  }
}

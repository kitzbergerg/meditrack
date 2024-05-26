import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ButtonModule} from "primeng/button";
import {InputNumberModule} from "primeng/inputnumber";
import {NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-min-rest-period-rule',
  standalone: true,
  imports: [
    ButtonModule,
    InputNumberModule,
    NgIf,
    RippleModule,
    FormsModule
  ],
  templateUrl: './min-rest-period-rule.component.html',
  styleUrl: './min-rest-period-rule.component.scss'
})
export class MinRestPeriodRuleComponent {
  @Input() minRestPeriod: number | null = null;
  editMode = false;
  @Output() updateMinRestPeriod = new EventEmitter<number | null>();

  constructor() {
    if (this.minRestPeriod == null) {
      this.minRestPeriod = 1
    }
  }

  update() {
    this.editMode = false
    this.updateMinRestPeriod.emit(this.minRestPeriod)
  }

  delete() {
    this.updateMinRestPeriod.emit(null)
  }

}

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
  editMode = true;
  @Output() updateMinRestPeriod = new EventEmitter<number | null>();
  duration: number | null = this.minRestPeriod;

  constructor() {
    if (this.minRestPeriod == null) {
      //this.minRestPeriod = {duration: 1};
      //TODO duration = 1
      //this.duration = 1
    }
  }

  update() {
    this.editMode = false
    this.updateMinRestPeriod.emit(this.duration)
  }

  delete() {
    this.updateMinRestPeriod.emit(null)
  }

}

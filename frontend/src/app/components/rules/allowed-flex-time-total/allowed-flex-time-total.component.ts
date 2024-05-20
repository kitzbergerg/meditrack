import {Component, EventEmitter, Input, Output} from '@angular/core';
import {RulesService} from "../../../services/rules.service";
import {ButtonModule} from "primeng/button";
import {InputNumberModule} from "primeng/inputnumber";
import {NgIf} from "@angular/common";
import {RippleModule} from "primeng/ripple";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-allowed-flex-time-total',
  standalone: true,
  imports: [
    ButtonModule,
    InputNumberModule,
    NgIf,
    RippleModule,
    FormsModule
  ],
  templateUrl: './allowed-flex-time-total.component.html',
  styleUrl: './allowed-flex-time-total.component.scss'
})
export class AllowedFlexTimeTotalComponent {
  @Input() allowedFlexTimeTotal: number | null = null;
  editMode = true;
  @Output() deleteAllowedFlexTimeTotal = new EventEmitter<void>();

  constructor(rulesService: RulesService) {
    if (this.allowedFlexTimeTotal == null) {
      this.allowedFlexTimeTotal = 1;
    }
  }

  update() {
    this.editMode = false
    console.log("")
  }

  delete() {
    this.deleteAllowedFlexTimeTotal.emit()
  }

}

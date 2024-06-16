import {Component, Input} from '@angular/core';
import {ShiftType} from "../../../interfaces/shiftType";
import {KeyValuePipe, NgForOf, NgStyle} from "@angular/common";
import {TableModule} from "primeng/table";
import {CardModule} from "primeng/card";

@Component({
  selector: 'app-schedule-legend',
  standalone: true,
  imports: [
    NgStyle,
    NgForOf,
    KeyValuePipe,
    TableModule,
    CardModule
  ],
  templateUrl: './schedule-legend.component.html',
  styleUrl: './schedule-legend.component.scss'
})
export class ScheduleLegendComponent {
  @Input() shiftTypes: { [id: string]: ShiftType } = {};

}

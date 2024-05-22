import {Component, Input} from '@angular/core';
import {Day} from "../../../interfaces/schedule.models";
import {ShiftCellComponent} from "../shift-cell/shift-cell.component";
import {NgForOf, NgIf, NgStyle} from "@angular/common";
import {TableModule} from "primeng/table";

@Component({
  selector: 'app-day-column',
  standalone: true,
  imports: [
    ShiftCellComponent,
    NgForOf,
    TableModule,
    NgStyle,
    NgIf
  ],
  templateUrl: './day-column.component.html',
  styleUrl: './day-column.component.scss'
})
export class DayColumnComponent {
  private _day: Day = { day: 0, shifts: [] };

  @Input()
  set day(value: Day) {
    this._day = value || { day: 0, shifts: [] };
  }

  get day(): Day {
    return this._day;
  }

}

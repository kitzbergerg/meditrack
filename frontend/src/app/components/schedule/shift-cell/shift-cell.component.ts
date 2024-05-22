import {Component, Input, OnInit} from '@angular/core';
import {Shift} from "../../../interfaces/schedule.models";
import {NgIf, NgStyle} from "@angular/common";

@Component({
  selector: 'app-shift-cell',
  standalone: true,
  imports: [
    NgStyle,
    NgIf
  ],
  templateUrl: './shift-cell.component.html',
  styleUrl: './shift-cell.component.scss'
})
export class ShiftCellComponent implements OnInit{

  @Input() shift: Shift | undefined;


  ngOnInit(): void { console.log(this.shift)}
}

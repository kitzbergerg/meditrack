import { Component } from '@angular/core';
import {HeaderComponent} from "./header/header.component";
import {WeekViewComponent} from "./week-view/week-view.component";

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [
    HeaderComponent,
    WeekViewComponent
  ],
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.scss'
})
export class ScheduleComponent {

}

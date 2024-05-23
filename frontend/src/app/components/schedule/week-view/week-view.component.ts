import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Day} from "../../../interfaces/schedule.models";
import {NgForOf, NgIf, NgStyle} from "@angular/common";
import {Table, TableModule} from "primeng/table";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";

@Component({
  selector: 'app-week-view',
  standalone: true,
  imports: [
    NgForOf,
    TableModule,
    NgStyle,
    NgIf,
    ButtonModule,
    InputTextModule,
    FormsModule,
    DropdownModule
  ],
  templateUrl: './week-view.component.html',
  styleUrl: './week-view.component.scss'
})
export class WeekViewComponent implements OnChanges {

  @Input() days: Day[] = [];
  @Input() employees: any[] = [];
  @Input() startDate: Date | undefined;
  @Output() weekChange = new EventEmitter<number>();
  roles: string[] = ['nurse', 'qualified nurse']; //TODO: fetch roles from backend
  weekNumber: number | undefined;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.startDate) {
      this.weekNumber = this.getWeekNumber(this.startDate);
    }
  }

  onGlobalFilter(table: Table, event: Event) {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }

  getWeekNumber(date: Date): number {
    // Copy date so that we don't modify the original date object
    const currentDate = new Date(date.getTime());

    // Set the date to the nearest Thursday: currentDate + 4 - currentDayNumber
    // Make Sunday (0) the last day of the week
    currentDate.setDate(currentDate.getDate() + 4 - (currentDate.getDay() || 7));

    // Get the first day of the year
    const yearStart = new Date(currentDate.getFullYear(), 0, 1);

    // Calculate the difference in milliseconds
    const diffInMs = currentDate.getTime() - yearStart.getTime();

    // Calculate full weeks to the nearest Thursday
    return Math.ceil((((diffInMs / 86400000) + 1) / 7));
  }

  getFormattedDateRange(): string {
    if (this.startDate) {
      const endDate = new Date(this.startDate);
      endDate.setDate(this.startDate.getDate() + 6);
      return `${this.startDate.toLocaleDateString('en-GB')} - ${endDate.toLocaleDateString('en-GB')}`;
    }
    return "";
  }

  previousWeek(): void {
    this.weekChange.emit(-1);
  }

  nextWeek(): void {
    this.weekChange.emit(1);
  }

}

import {Component} from '@angular/core';
import {CalendarModule} from "primeng/calendar";
import {ColorPickerModule} from "primeng/colorpicker";
import {DropdownModule} from "primeng/dropdown";
import {MultiSelectModule} from "primeng/multiselect";
import {DatePipe, NgForOf, NgIf, NgStyle} from "@angular/common";
import {OverlayModule} from "primeng/overlay";
import {PaginatorModule} from "primeng/paginator";
import {ConfirmationService, MessageService, SharedModule} from "primeng/api";
import {ToastModule} from "primeng/toast";
import {CardModule} from "primeng/card";
import {DialogModule} from "primeng/dialog";
import {User} from "../../interfaces/user";
import {UserService} from "../../services/user.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {PreferencesService} from "../../services/preferences.service";
import {Preferences} from "../../interfaces/preferences";
import {ConfirmDialogModule} from "primeng/confirmdialog";

@Component({
  selector: 'app-holidays',
  templateUrl: './preferences.component.html',
  standalone: true,
  imports: [
    CalendarModule,
    ColorPickerModule,
    DropdownModule,
    MultiSelectModule,
    NgForOf,
    NgIf,
    OverlayModule,
    PaginatorModule,
    SharedModule,
    ToastModule,
    CardModule,
    DatePipe,
    DialogModule,
    NgStyle,
    ConfirmDialogModule
  ],
  styleUrl: './preferences.component.scss'
})
export class PreferencesComponent {

  userId = '';
  loading = true;
  currentUser: User | undefined;

  teamUsers: User[] = []
  preference: Preferences | undefined;
  offDays: Date[] = [];

  selectedDate: Date | undefined;
  offDayDialog = false;
  valid = true;

  currentDate: Date = new Date();
  firstDayOfMonth: Date = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1);
  lastDayOfMonth: Date = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 2, 0);

  minDate: Date = this.firstDayOfMonth;
  maxDate: Date = this.lastDayOfMonth;

  constructor(private messageService: MessageService,
              private userService: UserService,
              private authorizationService: AuthorizationService,
              private preferencesService: PreferencesService,
              private confirmationService: ConfirmationService,
  ) {
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser();
    this.getPreferences();
  }

  getAllUsersFromTeam() {
    this.userService.getAllUserFromTeam().subscribe({
        next: response => {
          this.teamUsers = response;
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
      }
    );
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe({
      next: response => {
        this.currentUser = response;
        this.getAllUsersFromTeam();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }
    });
  }

  getPreferences() {
    this.preferencesService.getPreferences(this.userId).subscribe({
      next: response => {
        this.preference = response;
        this.offDays = response.offDays;
        this.offDays.sort((a, b) =>
          new Date(a).getTime() - new Date(b).getTime());
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }
    });
  }

  savePreferences() {
    if (this.preference !== undefined && this.preference.offDays.length !== 3) {
      if (this.selectedDate != undefined) {
        this.preference.offDays.push(this.selectedDate);
        this.preferencesService.createPreferences(this.preference).subscribe(
          {
            next: response => {
              this.messageService.add({
                severity: 'success',
                summary: 'Successfully Saved Off Day'
              });
              this.reset();
            }, error: error => {
              this.messageService.add({
                severity: 'error',
                summary: 'Saving Off Day Failed',
                detail: error.error
              });
              this.reset();
            }
          });
      }
      } else {
        this.messageService.add({
          severity: 'error',
          summary: 'Saving Off Day Failed',
          detail: 'Maximum of three off days allowed'
        });
        return;
    }
  }

  editPreferences(dayToChange: Date) {
    // TODO: change this to select it from calendar picker
    this.selectedDate = new Date();

    if (this.selectedDate != undefined && this.preference != undefined) {
      this.preference.offDays.filter(offDay => offDay !== dayToChange);
      this.preference.offDays.push(this.selectedDate);
      this.updatePreferences(this.preference);
    }
  }

  removePreferences(day: Date) {
    // remove this date from the array
    // if array empty then delete preference of user
    // if not update preference

    // TODO: when do we delete the whole preferences entity from the db?
    if (this.preference != undefined) {
      // filter out the chosen off day
      this.preference.offDays = this.preference.offDays.filter(offDay => offDay !== day);

      this.updatePreferences(this.preference);
    }
  }

  updatePreferences(preference: Preferences) {
    this.preferencesService.updatePreferences(preference).subscribe(
      {
        next: response => {
          this.messageService.add({
            severity: 'success',
            summary: 'Successfully Updated Preference'
          });
          this.reset();
        }, error: error => {
          this.messageService.add({
            severity: 'error',
            summary: 'Updating Preference Failed',
            detail: error.error
          });
          this.reset();
        }
      });
  }

  generateOffDay() {
    if (this.selectedDate != undefined) {
        this.valid = true;
        this.toggleDialog()
    } else {
      this.valid = false;
    }
  }

  toggleDialog() {
    this.offDayDialog = !this.offDayDialog
    this.valid = true;
  }

  isOffDay(date: any): boolean {
    if (date == undefined) return false;
    return this.findOffDay(date) != undefined;
  }

  findOffDay(date: any): Date | undefined {
    let currentDate = new Date();

    if (date instanceof Date) {
      currentDate = date;
    } else {
      currentDate = new Date(date.year, date.month, date.day);
    }

    currentDate.setHours(currentDate.getHours() + 2);

    return this.preference?.offDays.find(day => {
      return day.getTime() === currentDate.getTime();
    });
  }

  confirmDeleteOffDay(event: Event, offDay: Date) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to delete this off day?',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-success p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "pi pi-check mr-2",
      rejectIcon: "none",

      accept: () => {
        this.removePreferences(offDay);
      }
    });
  }

  reset() {
    this.selectedDate = undefined;
    this.offDays = [];
    this.getPreferences();
  }
}

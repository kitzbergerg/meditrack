import {Component} from '@angular/core';
import {Holiday, HolidayRequestStatus} from "../../interfaces/holiday";
import {HolidaysService} from "../../services/holidays.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {MessageService} from "primeng/api";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-holidays',
  templateUrl: './holidays.component.html',
  styleUrl: './holidays.component.scss'
})
export class HolidaysComponent {

  loading = true;
  holidays: Holiday[] = [];
  teamHolidays: Holiday[] = [];
  userIdNameMap: Map<string, string> = new Map<string, string>();
  userId = '';
  isUserDm = false;
  loadingTeamHolidays = true;

  submitted = false;
  valid = false;
  validationMessage = '';
  holiday: Holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };
  teamHoliday: Holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };
  teamUserName: string | undefined = '';

  initialLoad = false;

  currentDate: Date = new Date();
  minDate: Date = new Date();
  maxDate: Date = new Date(this.currentDate.getFullYear() + 1, 11, 31);
  selectedStartDate: Date | undefined;
  selectedEndDate: Date | undefined;

  formTitle= '';
  formAction= '';
  formMode: 'create' | 'edit' | 'details' = 'details';

  constructor(private holidaysService: HolidaysService,
              private authorizationService: AuthorizationService,
              private messageService: MessageService,
              private userService: UserService
  ) { }

  ngOnInit() {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getAllHolidaysFromUser();
    this.isUserDm = this.authorizationService.hasAuthority(["admin", "dm"]);
    if (this.isUserDm) {
      this.getAllHolidaysFromTeam();
      this.userIdNameMap = this.getTeamUsersToMap();
    }
    this.currentDate.setHours(this.currentDate.getHours() + 2)
    this.minDate.setHours(0, 0, 0, 0)
  }

  getAllHolidaysFromUser() {
    this.holidaysService.getAllHolidaysByUser()
      .subscribe({
        next: (response) => {
          this.holidays = response.sort((a, b) => Date.parse(a.startDate) - Date.parse(b.startDate));
          this.loading = false;
          if (this.holidays.length === 0) {
            this.formMode = 'create';
          }
          if (this.holidays.length > 0 && !this.initialLoad) {
            this.initialLoad = true;
            this.selectHoliday(this.holidays[0])
          }
        }, error: (error) => {
          console.error('Error fetching data:', error);
        }
      });
  }

  getHolidayById(id: string) {
    this.holidaysService.getHolidayByIdAndUser(id)
      .subscribe({
        next: (response) => {
          this.holiday = response;
          this.getAllHolidaysFromUser();
        }, error: (error) => {
          console.error('Error fetching data:', error);
        }
      });
  }

  createHoliday() {
    this.submitted = true;
    if (this.valid) {
      const holiday: Holiday = {
        id: undefined,
        startDate: this.holiday.startDate,
        endDate: this.holiday.endDate,
        status: undefined,
        user: undefined
      };
      this.holidaysService.createHoliday(holiday)
        .subscribe({
          next: (response) => {
            console.log('Holiday created successfully:', response);
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Holiday created successfully'});
            this.getAllHolidaysFromUser();
            this.resetForm();
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Creating holiday failed', detail: error.error});
          }
        });
    } else {
      this.messageService.add({severity: 'warn', summary: 'Validation failed',
        detail: 'Validation failed! ' + this.validationMessage});
    }
  }

  updateHoliday() {
    this.submitted = true;
    if (this.valid) {
      this.holiday.status = undefined;
      this.holidaysService.updateHoliday(this.holiday)
        .subscribe({
          next: (response) => {
            console.log('Holiday successfully updated:', response);
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Holiday updated successfully'});
            this.selectHoliday(this.holiday);
            this.resetForm();
            if (this.isUserDm) {
              this.getAllHolidaysFromTeam();
            }
          }, error: (error) => {
            this.messageService.add({severity: 'error', summary: 'Updating holiday failed', detail: error.error});
          }
        });
    } else {
      this.messageService.add({severity: 'warn', summary: 'Validation failed', detail: this.validationMessage});
    }
  }

  showCreateForm() {
    this.resetForm();
    this.formMode = 'create';
    this.selectedStartDate = undefined;
    this.selectedEndDate = undefined;
  }

  selectHoliday(holiday: Holiday){
    if (holiday.id != undefined) {
      this.getHolidayById(holiday.id);
      this.selectedStartDate = new Date(holiday.startDate);
      this.selectedStartDate.setHours(this.currentDate.getHours());
      this.selectedEndDate = new Date(holiday.endDate);
      this.selectedEndDate.setHours(this.currentDate.getHours())
      this.formMode = 'details';
    }
  }

  editHoliday() {
    this.formMode = 'edit';
  }

  getFormTitle() {
    if (this.formMode === 'create') {
      this.formTitle = 'Request Holiday';
      this.formAction = 'Submit';
    } else if (this.formMode === 'edit') {
      this.formTitle = 'Edit Holiday';
      this.formAction = 'Request';
    } else {
      this.formTitle = 'Holiday Details';
      this.formAction = 'Request';
    }
    return this.formTitle;
  }

  createOrUpdateHoliday() {
    this.valid = this.validateHoliday();
    if (this.formMode === 'create') {
      this.createHoliday();
    } else if (this.formMode === 'edit') {
      this.updateHoliday();
      this.getFormTitle();
      this.getAllHolidaysFromUser();
    }
  }

  validateHoliday() {
    if (this.formMode != 'create' && this.holiday.status !== HolidayRequestStatus.REQUESTED) {
      this.validationMessage = 'Holiday status must be requested!';
      return false;
    }
    if (this.holiday.startDate === '' || this.holiday.endDate === '') {
      this.validationMessage = 'Start date and end date must be set!';
      return false;
    }
    const startDate = Date.parse(this.holiday.startDate);
    const endDate = Date.parse(this.holiday.endDate);
    const today = new Date().setHours(0, 0, 0, 0);

    if (startDate > endDate || startDate < today) {
      this.validationMessage = 'End date must be after start date and dates must be in the future!';
      return false;
    }

    //check if the holiday is overlapping with another holiday
    for (let i = 0; i < this.holidays.length; i++) {
      const holiday = this.holidays[i];
      if (holiday.id !== this.holiday.id) {
        const holidayStartDate = Date.parse(holiday.startDate);
        const holidayEndDate = Date.parse(holiday.endDate);

        if ((startDate >= holidayStartDate && startDate <= holidayEndDate) ||
            (endDate >= holidayStartDate && endDate <= holidayEndDate) ||
            (startDate <= holidayStartDate && endDate >= holidayEndDate)) {
          if (holiday.status === HolidayRequestStatus.APPROVED || holiday.status === HolidayRequestStatus.REQUESTED) {
            this.validationMessage = 'Holiday is overlapping with another holiday!';
            return false;
          }
        }
      }
    }

    return true;
  }

  resetForm() {
    this.submitted = false;
    this.holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };
    this.formMode = 'details';
  }

  cancelEditing() {
    this.selectHoliday(this.holiday);
    this.resetForm();
  }

  getAllHolidaysFromTeam() {
    this.holidaysService.getAllHolidaysByTeam()
      .subscribe({
        next: (response) => {
          this.teamHolidays = response
            .sort((a, b) => Date.parse(a.startDate) - Date.parse(b.startDate));
          this.loadingTeamHolidays = false;
          this.selectTeamHoliday(this.findFirstRequestedHoliday());
        }, error: (error) => {
          console.error('Error fetching data:', error);
        }
      })
  }

  selectTeamHoliday(holiday: Holiday | undefined) {
    if (holiday === undefined) {
      return;
    }
    this.teamHoliday = holiday;
    if (holiday.user != undefined) {
      this.teamUserName = this.getUsernameFromId(holiday.user);
    }
  }

  getTeamUsersToMap() {
    const map = new Map<string, string>();
    this.userService.getAllUserFromTeam()
      .subscribe({
        next: (response) => {
          response.forEach(user => {
            if (user.id != undefined && user.username != undefined) {
              map.set(user.id, user.username);
            }
          });
          this.selectTeamHoliday(this.findFirstRequestedHoliday());
        }, error: (error) => {
          console.error('Error fetching data:', error);
        }
      });
    return map;
  }

  findFirstRequestedHoliday() {
    return this.teamHolidays.find(holiday => holiday.status === HolidayRequestStatus.REQUESTED);
  }

  getUsernameFromId(id: string) {
    if (this.userIdNameMap.has(id)) {
      return this.userIdNameMap.get(id)?.split('_')[0];
    }
    return '';
  }

  updateStatus(status: string) {
    if (this.teamHoliday.id === undefined) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'Status of holiday could not be updated!'});
      return;
    }
    this.holidaysService.updateHolidayStatus(this.teamHoliday.id, status)
      .subscribe({
        next: (response) => {
          console.log('Holiday status updated successfully:', response);
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Holiday status updated successfully'});
          this.getAllHolidaysFromTeam();
          this.getAllHolidaysFromUser(); // Refresh user holidays because the dm can also approve his own holidays
        }, error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Updating holiday failed', detail: error.error});
        }
      });
  }

  isApprovable() {
    return this.teamHoliday.status === HolidayRequestStatus.REQUESTED;
  }

  isCancelable() {
    return this.teamHoliday.status === HolidayRequestStatus.APPROVED;
  }

  isEditable() {
    return this.holiday.status === HolidayRequestStatus.REQUESTED;
  }

  setStartDate() {
    if (this.selectedStartDate != undefined) {
      this.selectedStartDate.setHours(this.currentDate.getHours());
      this.holiday.startDate = this.selectedStartDate.toISOString().split('T')[0];
    }
  }

  setEndDate() {
    if (this.selectedEndDate != undefined) {
      this.selectedEndDate.setHours(this.currentDate.getHours());
      this.holiday.endDate = this.selectedEndDate.toISOString().split('T')[0];
    }
  }

  getFormattedDateFromString(date: string) {
    return new Date(date).toLocaleDateString('de-DE', { year: 'numeric', month: '2-digit', day: '2-digit' });
  }

  protected readonly HolidayRequestStatus = HolidayRequestStatus;
}

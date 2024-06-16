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
  holiday: Holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };
  teamHoliday: Holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };
  teamUserName: string | undefined = '';

  initialLoad = false;

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
      this.messageService.add({severity: 'warn', summary: 'Validation failed', detail: 'Validation failed! End date' +
          ' must be after start date and dates must be in the future!'});
    }
  }

  updateHoliday() {
    this.submitted = true;
    if (this.valid) {
      this.holiday.status = HolidayRequestStatus.REQUESTED;
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
      this.messageService.add({severity: 'warn', summary: 'Validation failed', detail: 'Validation failed! End date' +
          ' must be after start date and dates must be in the future!'});
    }
  }

  showCreateForm() {
    this.resetForm();
    this.formMode = 'create';
  }

  selectHoliday(holiday: Holiday){
    if (holiday.id != undefined) {
      this.getHolidayById(holiday.id);
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
    if (this.holiday.startDate === '' || this.holiday.endDate === '') {
      return false;
    }
    let startDate = Date.parse(this.holiday.startDate);
    let endDate = Date.parse(this.holiday.endDate);

    return (startDate <= endDate && startDate > Date.now());
  }

  resetForm() {
    this.submitted = false;
    this.holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };
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

  protected readonly HolidayRequestStatus = HolidayRequestStatus;
}

import {Component} from '@angular/core';
import {Holiday} from "../../interfaces/holiday";
import {HolidaysService} from "../../services/holidays.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-holidays',
  templateUrl: './holidays.component.html',
  styleUrl: './holidays.component.scss'
})
export class HolidaysComponent {

  loading = true;
  holidays: Holiday[] = [];
  userId = '';

  submitted = false;
  valid = false;
  holiday: Holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };

  initialLoad = false;

  formTitle= 'Request Holiday';
  formAction= 'Submit';
  formMode: 'create' | 'edit' = 'create';

  constructor(private holidaysService: HolidaysService,
              private authorizationService: AuthorizationService,
              private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getAllHolidaysFromUser();
    this.showCreateForm();
  }

  getAllHolidaysFromUser(): void {
    this.holidaysService.getAllHolidaysByUser()
      .subscribe({
        next: (response) => {
          this.holidays = response.sort((a, b) => Date.parse(a.startDate) - Date.parse(b.startDate));
          this.loading = false;
          if (this.holidays.length > 0 && !this.initialLoad) {
            this.initialLoad = true;
          }
        }, error: (error) => {
          console.error('Error fetching data:', error);
        }
      });
  }

  getHolidayById(id: string): void {
    this.holidaysService.getHolidayByIdAndUser(id)
      .subscribe({
        next: (response) => {
          this.holiday = response;
        }, error: (error) => {
          console.error('Error fetching data:', error);
        }
      });
  }

  createHoliday(): void {
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
          ' must be same as start or after and dates must be in the future!'});
    }
  }

  updateHoliday(): void {
    //todo
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

  selectHoliday(holiday: Holiday){
    if (holiday.id != undefined) {
      this.getHolidayById(holiday.id);
      this.formMode = 'edit';
    }
  }

  showCreateForm() {
    this.resetForm();
    this.formMode = 'create';
  }

  resetForm() {
    this.submitted = false;
    this.holiday = { id: undefined, startDate: '', endDate: '', status: undefined, user: undefined };
  }

  getFormTitle(): string {
    if (this.formMode === 'edit') {
      this.formTitle = 'Edit holiday';
      this.formAction = 'Request change';
    } else {
      this.formTitle = 'Request holiday';
      this.formAction = 'Submit';
    }
    return this.formTitle;
  }

  editHoliday() {
    this.formMode = 'edit';
  }

  cancelEditing() {
    this.selectHoliday(this.holiday);
    this.resetForm();
  }

}

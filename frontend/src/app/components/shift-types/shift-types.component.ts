import {Component, ChangeDetectorRef} from '@angular/core';
import {ShiftType, ShiftTypeCreate} from "../../interfaces/shiftType";
import {ShiftService} from "../../services/shift.service";

@Component({
  selector: 'app-shift-types',
  templateUrl: './shift-types.component.html',
  styleUrl: './shift-types.component.scss'
})
export class ShiftTypesComponent {

  shiftTypes: ShiftType[] = [];

  dropdownOptions: { name: string }[] = [
    {name: 'Choose Type'},
    {name: 'Day'},
    {name: 'Night'}
  ];
  selectedOption: { name: string } = this.dropdownOptions[0];

  shiftType: ShiftType = {
    id: 0,
    name: '',
    startTime: '',
    endTime: '',
    breakStartTime: '',
    breakEndTime: '',
    type: this.selectedOption.name,
    color: '#ff0000',
    abbreviation: ''
  };

  initialLoad: boolean = false;

  submitted = false;
  valid = false;

  emptyTime: Date | null = null;
  startTimeDate: Date | null = this.emptyTime;
  endTimeDate: Date | null = this.emptyTime;
  breakStartTimeDate: Date | null = this.emptyTime;
  breakEndTimeDate: Date | null = this.emptyTime;

  formTitle: string = '';
  formAction: string = '';
  formMode: 'create' | 'edit' | 'details' = 'details';

  constructor(private shiftService: ShiftService,
              private cdr: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {
    this.loadShiftTypes();
  }

  loadShiftTypes(): void {
    this.shiftService.getAllShiftTypes()
      .subscribe(fetchedShiftTypes => {
        this.shiftTypes = fetchedShiftTypes;
        if (this.shiftTypes.length === 0) {
          this.formMode = 'create';
        }
        if (this.shiftTypes.length > 0 && !this.initialLoad) {
          this.initialLoad = true;
          this.selectShiftType(this.shiftTypes[0]);
        }
      });
  }

  deleteShiftType(id: number): void {
    this.shiftService.deleteShiftType(id)
      .subscribe(response => {
        console.log('Shift Type deleted successfully');
        this.loadShiftTypes();
        this.resetForm();
      }, error => {
        console.error('Error deleting shift type:', error);
      });
  }

  getShiftType(id: number) {
    this.shiftService.getShiftType(id)
      .subscribe((response: ShiftType) => {
        console.log('Shift Type retrieved successfully:', response);
        this.shiftType = response;

        this.startTimeDate = this.getTime(this.shiftType.startTime);
        this.endTimeDate = this.getTime(this.shiftType.endTime);
        this.breakStartTimeDate = this.getTime(this.shiftType.breakStartTime);
        this.breakEndTimeDate = this.getTime(this.shiftType.breakEndTime);

        this.selectedOption.name = this.shiftType.type;

        this.loadShiftTypes();
      }, error => {
        console.error('Error retrieving Shift Type:', error);
      });
  }

  getTime(givenTime: string) {
    const [hours, minutes, seconds] = givenTime.split(':').map(Number);
    const timeDate = new Date();
    timeDate.setHours(hours, minutes, seconds || 0);
    return timeDate;
  }

  createShiftType() {
    this.submitted = true;

    if (this.valid) {
      const newShiftType: ShiftTypeCreate = {
        name: this.shiftType.name,
        startTime: this.startTimeDate ? this.startTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : '',
        endTime: this.endTimeDate ? this.endTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : '',
        breakStartTime: this.breakStartTimeDate ? this.breakStartTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : '',
        breakEndTime: this.breakEndTimeDate ? this.breakEndTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : '',
        type: this.selectedOption.name,
        color: this.shiftType.color,
        abbreviation: this.shiftType.abbreviation
      };

      this.shiftService.createShiftType(newShiftType)
        .subscribe(response => {
          console.log('Shift Type created successfully:', response);
          this.loadShiftTypes();
          this.resetForm();
        }, error => {
          console.error('Error creating shift type:', error);
        });
    }
  }

  updateShiftType() {
    this.submitted = true;

    if (this.valid) {
      const shiftTypeToUpdate: ShiftType = {
        id: this.shiftType.id,
        name: this.shiftType.name,
        startTime: this.startTimeDate ? this.startTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : this.shiftType.startTime,
        endTime: this.endTimeDate ? this.endTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : this.shiftType.endTime,
        breakStartTime: this.breakStartTimeDate ? this.breakStartTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : this.shiftType.breakStartTime,
        breakEndTime: this.breakEndTimeDate ? this.breakEndTimeDate.toLocaleTimeString('en-US', {
          hour12: false,
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }) : this.shiftType.breakEndTime,
        type: this.shiftType.type,
        color: this.shiftType.color,
        abbreviation: this.shiftType.abbreviation
      };

      this.shiftService.updateShiftType(shiftTypeToUpdate)
        .subscribe(response => {
          console.log('Shift Type updated successfully:', response);
          this.resetForm();
          this.selectShiftType(shiftTypeToUpdate);
        }, error => {
          console.error('Error updating shift type:', error);
        });
    }
  }

  showCreateForm() {
    this.resetForm();
    this.formMode = 'create';
  }

  selectShiftType(shiftType: ShiftType) {
    this.getShiftType(shiftType.id);
    this.formMode = 'details';
  }

  editShiftType() {
    this.formMode = 'edit';
  }

  getFormTitle(): string {
    if (this.formMode === 'create') {
      this.formTitle = 'Create Shift Type';
      this.formAction = 'Create';
    } else if (this.formMode === 'edit') {
      this.formTitle = 'Edit Shift Type';
      this.formAction = 'Save';
    } else {
      this.formTitle = 'Shift Type Details';
      this.formAction = 'Edit';
    }
    return this.formTitle;
  }

  createOrUpdateShiftType() {
    this.valid = (this.shiftType.name !== '') && (this.shiftType.startTime !== '')
      && (this.shiftType.endTime !== '') && (this.shiftType.breakStartTime !== '')
      && (this.shiftType.breakEndTime !== '') && (this.shiftType.type !== this.dropdownOptions[0].name)
      && (this.shiftType.color !== '') && (this.shiftType.abbreviation !== '');

    if (this.formMode === 'create') {
      this.createShiftType();
    } else if (this.formMode === 'edit') {
      this.updateShiftType();
      this.getFormTitle();
      this.loadShiftTypes();
    }
  }

  cancelEditing() {
    this.resetForm();
    this.selectShiftType(this.shiftType);
  }

  onColorChange(event: any) {
    this.shiftType.color = event.value;
    this.cdr.detectChanges();
  }

  resetForm() {
    this.submitted = false;
    this.selectedOption.name = 'Choose Type';
    this.startTimeDate = this.emptyTime;
    this.endTimeDate = this.emptyTime;
    this.breakStartTimeDate = this.emptyTime;
    this.breakEndTimeDate = this.emptyTime;
    this.shiftType = {
      id: 0,
      name: '',
      startTime: '',
      endTime: '',
      breakStartTime: '',
      breakEndTime: '',
      type: this.selectedOption.name,
      color: '#ff0000',
      abbreviation: ''
    };
  }
}

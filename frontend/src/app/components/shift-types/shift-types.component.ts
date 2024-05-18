import { Component } from '@angular/core';
import {ShiftType, ShiftTypeCreate, TypeEnum} from "../../interfaces/shiftTypeInterface";
import {ShiftService} from "../../services/shift.service";

@Component({
  selector: 'app-shift-types',
  templateUrl: './shift-types.component.html',
  styleUrl: './shift-types.component.scss'
})
export class ShiftTypesComponent {

    // TODO: fix attribute color - it's not saved after picking from ColorPickerModule

    shiftTypes: ShiftType[] = [];
    newShiftType: ShiftTypeCreate = { name: '', startTime: '', endTime: '', breakStartTime: '', breakEndTime: '', type: TypeEnum.Default,  color: '', abbreviation: ''};
    currentShiftType: ShiftType = { id: -1, name: '', startTime: '', endTime: '', breakStartTime: '', breakEndTime: '', type: TypeEnum.Default,  color: '', abbreviation: '' };

    initialLoad: boolean = false;

    emptyTime: Date | null = null;
    startTimeDate: Date | null = this.emptyTime;
    endTimeDate: Date | null = this.emptyTime;
    breakStartTimeDate: Date | null = this.emptyTime;
    breakEndTimeDate: Date | null = this.emptyTime;

    dropdownOptions: { name: string }[] = [
      { name: 'Choose Type' },
      { name: 'Day' },
      { name: 'Night' }
    ];
    selectedOption: { name: string } = this.dropdownOptions[0];

    formTitle: string = '';
    formAction: string = '';
    formMode: 'create' | 'edit' | 'details' = 'details';

    constructor(private shiftService: ShiftService) { }

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
                this.currentShiftType = response;

                this.startTimeDate = this.getTime(this.currentShiftType.startTime);
                this.endTimeDate = this.getTime(this.currentShiftType.endTime);
                this.breakStartTimeDate = this.getTime(this.currentShiftType.breakStartTime);
                this.breakEndTimeDate = this.getTime(this.currentShiftType.breakEndTime);

                this.selectedOption.name = this.currentShiftType.type;

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
      this.newShiftType.startTime = this.startTimeDate ? this.startTimeDate.toISOString().split('T')[1].split('.')[0] : '';
      this.newShiftType.endTime = this.endTimeDate ? this.endTimeDate.toISOString().split('T')[1].split('.')[0] : '';
      this.newShiftType.breakStartTime = this.breakStartTimeDate ? this.breakStartTimeDate.toISOString().split('T')[1].split('.')[0] : '';
      this.newShiftType.breakEndTime = this.breakEndTimeDate ? this.breakEndTimeDate.toISOString().split('T')[1].split('.')[0] : '';
        this.shiftService.createShiftType(this.newShiftType)
          .subscribe(response => {
            console.log('Shift Type created successfully:', response);
            this.loadShiftTypes();
            this.resetForm();
          }, error => {
            console.error('Error creating shift type:', error);
          });
    }

  updateShiftType() {
        const shiftTypeToUpdate: ShiftType = {
            id: this.currentShiftType.id,
            name: this.currentShiftType.name,
            startTime: this.startTimeDate ? this.startTimeDate.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' }) : this.currentShiftType.startTime,
            endTime: this.endTimeDate ? this.endTimeDate.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' }) : this.currentShiftType.endTime,
            breakStartTime: this.breakStartTimeDate ? this.breakStartTimeDate.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' }) : this.currentShiftType.breakStartTime,
            breakEndTime: this.breakEndTimeDate ? this.breakEndTimeDate.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' }) : this.currentShiftType.breakEndTime,
            type: this.currentShiftType.type,
            color: this.currentShiftType.color,
            abbreviation: this.currentShiftType.abbreviation
        };

        this.shiftService.updateShiftType(shiftTypeToUpdate)
            .subscribe(response => {
                console.log('Shift Type updated successfully:', response);
                this.resetForm();
                // update shown shift type and list (case: name was changed)
                this.selectShiftType(this.currentShiftType);
            }, error => {
                console.error('Error updating shift type:', error);
            });
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
      this.selectShiftType(this.currentShiftType);
    }

    resetForm() {
      this.selectedOption.name = 'Choose Type';
      this.startTimeDate = this.emptyTime;
      this.endTimeDate = this.emptyTime;
      this.breakStartTimeDate = this.emptyTime;
      this.breakEndTimeDate = this.emptyTime;
      this.newShiftType = { name: '', startTime: '', endTime: '', breakStartTime: '', breakEndTime: '', type: TypeEnum.Default, color: '', abbreviation: '' };
    }
}

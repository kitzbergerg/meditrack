import { Component } from '@angular/core';
import {ShiftType, ShiftTypeCreate} from "../../interfaces/shiftTypeInterface";
import {ShiftService} from "../../services/shift.service";

@Component({
  selector: 'app-shift-types',
  templateUrl: './shift-types.component.html',
  styleUrl: './shift-types.component.scss'
})
export class ShiftTypesComponent {

    shiftTypes: ShiftType[] = [];
    newShiftType: ShiftTypeCreate = { name: '', startTime: '', endTime: '' };
    currentShiftType: ShiftType = { id: 0, name: '', startTime: '', endTime: '' };
    showOneTypeContainer: boolean = false;
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
          });
    }

    deleteShiftType(id: number): void {
        this.shiftService.deleteShiftType(id)
          .subscribe(response => {
            console.log('Shift Type deleted successfully');
            this.showOneTypeContainer = false;
            this.loadShiftTypes();
          }, error => {
            console.error('Error deleting shift type:', error);
          });
    }

    getShiftType(id: number) {
        this.shiftService.getShiftType(id)
            .subscribe((response: ShiftType) => {
                console.log('Shift Type retrieved successfully:', response);
                this.currentShiftType = response;
                this.loadShiftTypes();
            }, error => {
                console.error('Error retrieving Shift Type:', error);
            });
    }

    createShiftType() {
        this.shiftService.createShiftType(this.newShiftType)
          .subscribe(response => {
            console.log('Shift Type created successfully:', response);
            this.loadShiftTypes();
            this.resetForm();
          }, error => {
            console.error('Error creating shift type:', error);
          });
        this.resetForm();
    }

    updateShiftType() {
        const shiftTypeToUpdate: ShiftType = {
            id: this.currentShiftType.id,
            name: this.currentShiftType.name,
            startTime: this.currentShiftType.startTime,
            endTime: this.currentShiftType.endTime
        };

        this.shiftService.updateShiftType(shiftTypeToUpdate)
            .subscribe(response => {
                console.log('Shift Type updated successfully:', response);
                // Reset the form
                this.resetForm();
                // update shown shift type and list (case: name was changed)
                this.selectShiftType(this.currentShiftType);
            }, error => {
                console.error('Error updating shift type:', error);
            });
    }

    showCreateForm() {
        this.showOneTypeContainer = true;
        this.formMode = 'create';
        this.resetForm();
    }

    selectShiftType(shiftType: ShiftType) {
        this.getShiftType(shiftType.id);
        this.showOneTypeContainer = true;
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

    resetForm() {
        this.newShiftType = { name: '', startTime: '', endTime: '' };
    }
}

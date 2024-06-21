import {Component, ChangeDetectorRef} from '@angular/core';
import {ShiftType} from "../../interfaces/shiftType";
import {ShiftTypeService} from "../../services/shift-type.service";
import {MessageService} from "primeng/api";
import {User} from "../../interfaces/user";
import {UserService} from "../../services/user.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {Team} from "../../interfaces/team";
import {Role} from "../../interfaces/role";
import {RolesService} from "../../services/roles.service";

@Component({
  selector: 'app-shift-types',
  templateUrl: './shift-types.component.html',
  styleUrl: './shift-types.component.scss'
})
export class ShiftTypesComponent {

  loading = true;
  teamComponentHeader = 'shift types';

  shiftTypes: ShiftType[] = [];

  roles: Role[] = [];

  shiftType: ShiftType = {
    id: 0,
    name: '',
    startTime: '',
    endTime: '',
    breakStartTime: '',
    breakEndTime: '',
    color: '#ff0000',
    abbreviation: ''
  };

  initialLoad = false;

  submitted = false;
  valid = false;

  emptyTime: Date | null = null;
  startTimeDate: Date | null = this.emptyTime;
  endTimeDate: Date | null = this.emptyTime;
  breakStartTimeDate: Date | null = this.emptyTime;
  breakEndTimeDate: Date | null = this.emptyTime;

  formTitle = '';
  formAction = '';
  formMode: 'create' | 'edit' | 'details' = 'details';
  userId = '';
  currentUser: User = {
    id: '',
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: '',
    roles: [],
    workingHoursPercentage: 0,
    currentOverTime: 0,
    specialSkills: [],
    holidays: [],
    shifts: [],
    role: {name: "", color: "", abbreviation: ""},
    team: undefined,
    requestedShiftSwaps: [],
    suggestedShiftSwaps: [],
    canWorkShiftTypes: [],
    preferredShiftTypes: []
  };

  constructor(private shiftService: ShiftTypeService,
              private cdr: ChangeDetectorRef,
              private messageService: MessageService,
              private userService: UserService,
              private authorizationService: AuthorizationService,
              private rolesService: RolesService
  ) {
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser();
  }

  receiveTeam(team: Team) {
    this.currentUser.team = team.id;
    this.loadShiftTypes()
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe({
      next: response => {
        this.currentUser = response;
        if (response.team != null) {
          this.loadShiftTypes();
          this.loadRoles();
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }}
    );
  }

  loadShiftTypes(): void {
    this.shiftService.getAllShiftTypesByTeam()
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

  loadRoles(): void {
    this.rolesService.getAllRolesFromTeam()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
      });
  }

  deleteShiftType(): void {
    if (this.shiftType.id != undefined) {
      this.shiftService.deleteShiftType(this.shiftType.id)
        .subscribe(response => {
          console.log('Shift Type deleted successfully');
          this.messageService.add({
            severity: 'success',
            summary: 'Successfully Deleted Shift Type ' + this.shiftType.name
          });
          this.loadShiftTypes();
          this.resetForm();
        }, error => {
          console.error('Error deleting shift type:', error);
          this.messageService.add({severity: 'error', summary: 'Deleting Shift Type Failed', detail: error.error});
        });
    }
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
      const newShiftType: ShiftType = {
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
        color: this.shiftType.color,
        abbreviation: this.shiftType.abbreviation,
        team: this.shiftType.team
      };

      this.shiftService.createShiftType(newShiftType)
        .subscribe(response => {
          console.log('Shift Type created successfully:', response);
          this.messageService.add({
            severity: 'success',
            summary: 'Successfully Created Shift Type ' + newShiftType.name
          });
          this.loadShiftTypes();
          this.resetForm();
        }, error => {
          //console.log(error.error);
          console.error('Error creating shift type:', error);
          if (error.error === "data integrity violation") {
            this.messageService.add({
              severity: 'error',
              summary: 'Creating Shift Type Failed',
              detail: "Name, Color and Abbreviation have to be unique."
            });
          } else {
            this.messageService.add({severity: 'error', summary: 'Creating Shift Type Failed', detail: error.error});
          }
        });
    } else {
      this.messageService.add({severity: 'warn', summary: 'Validation Failed', detail: 'Please read the warnings.'});
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
        color: this.shiftType.color,
        abbreviation: this.shiftType.abbreviation,
        team: this.shiftType.team
      };

      console.log(shiftTypeToUpdate)

      this.shiftService.updateShiftType(shiftTypeToUpdate)
        .subscribe(response => {
          this.messageService.add({
            severity: 'success',
            summary: 'Successfully Updated Shift Type ' + this.shiftType.name
          });
          this.resetForm();
          this.selectShiftType(shiftTypeToUpdate);
        }, error => {
          console.error('Error updating shift type:', error);
          if (error.error === "data integrity violation") {
            this.messageService.add({
              severity: 'error',
              summary: 'Updating Shift Type Failed',
              detail: "Name, Color and Abbreviation have to be unique."
            });
          } else {
            this.messageService.add({severity: 'error', summary: 'Updating Shift Type Failed', detail: error.error});
          }
        });
    } else {
      this.messageService.add({severity: 'warn', summary: 'Validation Failed', detail: 'Please read the warnings.'});
    }
  }

  showCreateForm() {
    this.resetForm();
    this.formMode = 'create';
  }

  selectShiftType(shiftType: ShiftType) {
    if (shiftType.id != undefined) {
      this.getShiftType(shiftType.id);
      this.formMode = 'details';
    }
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
    this.valid = (this.shiftType.name !== '') && (this.startTimeDate !== null)
      && (this.endTimeDate !== null) && (this.breakStartTimeDate !== null)
      && (this.breakEndTimeDate !== null) && (this.shiftType.color !== '')
      && (this.shiftType.abbreviation !== '');

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
      color: '#ff0000',
      abbreviation: ''
    };
  }
}

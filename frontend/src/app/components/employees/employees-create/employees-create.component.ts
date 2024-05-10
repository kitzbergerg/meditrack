import { Component } from '@angular/core';
import {AuthorizationService} from "../../../services/authentication/authorization.service";
import {UserService} from "../../../services/user.service";
import {User} from "../../../interfaces/user";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-employees',
  templateUrl: './employees-create.component.html',
  styleUrls: ['./employees-create.component.scss']
})
export class EmployeesCreateComponent {

  newUser: User = {
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
    requestedShiftSwaps: [],
    suggestedShiftSwaps: [],
    canWorkShiftTypes: [],
    preferredShiftTypes: []
  };

  submitted: boolean = false;
  showNewUserForm: boolean = false;
  employeeForm: UntypedFormGroup;


  constructor(private authorizationService: AuthorizationService,
              private userService: UserService,
              private formBuilder: UntypedFormBuilder
  ) {
    this.employeeForm = this.formBuilder.group(
      {
        username: [
          '',
          [Validators.required]
        ],
        password:[
          '',
          [Validators.required]
        ],
        email: [
          '',
          [Validators.required]
        ],
        firstName: [
          '',
          [Validators.required]
        ],
        lastName: [
          '',
          [Validators.required]
        ],
        workingHoursPercentage: [
          0,
          [Validators.required]
        ]
      }
    );
  }

  ngOnInit(): void {
    this.resetForm();
  }

  createUser() {
    this.submitted = true;
    if (this.employeeForm.valid) {
      console.log('Creating employee');
      this.newUser = this.employeeForm.value;
      this.userService.createUser(this.newUser)
        .subscribe(
        (response) => {
          console.log('User created successfully:', response);
          this.resetForm();
          this.newUser = this.employeeForm.value;
          this.showNewUserForm = false;
          this.submitted = true;
        },
        (error) => {
          console.error('Error creating user:', error);
        }
      );
    }
  }

  toggleNewUserForm() {
    this.showNewUserForm = !this.showNewUserForm;
    this.resetForm();
  }

  resetForm() {
    this.employeeForm = this.formBuilder.group({
      id: [null],
      username: ['', Validators.required],
      password: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      roles: [[]],
      role: [null],
      workingHoursPercentage: [0, [Validators.required, Validators.min(0)]],
      currentOverTime: [null],
      specialSkills: [[]],
      team: [null],
      holidays: [[]],
      preferences: [null],
      requestedShiftSwaps: [[]],
      suggestedShiftSwaps: [[]],
      shifts: [[]],
      canWorkShiftTypes: [[]],
      preferredShiftTypes: [[]]
    });
  }
}

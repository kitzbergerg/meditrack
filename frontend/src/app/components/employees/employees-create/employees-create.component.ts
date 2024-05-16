import { Component } from '@angular/core';
import {AuthorizationService} from "../../../services/authentication/authorization.service";
import {UserService} from "../../../services/user.service";
import {User} from "../../../interfaces/user";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {RolesService} from "../../../services/roles.service";
import {Role} from "../../../interfaces/roles/rolesInterface";


@Component({
  selector: 'app-employees',
  templateUrl: './employees-create.component.html',
  styleUrls: ['./employees-create.component.scss']
})
export class EmployeesCreateComponent {

  roles: Role[] = [];

  loggedInUser : User = {
    canWorkShiftTypes: [],
    email: "",
    firstName: "",
    holidays: [],
    lastName: "",
    password: "",
    preferredShiftTypes: [],
    requestedShiftSwaps: [],
    roles: [],
    shifts: [],
    specialSkills: [],
    suggestedShiftSwaps: [],
    workingHoursPercentage: 0
  };

  newUser: User = {
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: '',
    role: '',
    roles: [],
    workingHoursPercentage: 1.0,
    currentOverTime: null,
    specialSkills: [],
    holidays: [],
    shifts: [],
    requestedShiftSwaps: [],
    suggestedShiftSwaps: [],
    canWorkShiftTypes: [],
    preferredShiftTypes: []
  };

  submitted = false;
  showNewUserForm = false;
  employeeForm: UntypedFormGroup;


  constructor(private authorizationService: AuthorizationService,
              private userService: UserService,
              private formBuilder: UntypedFormBuilder,
              private rolesService: RolesService,
  ) {

    this.employeeForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
      email: ['', [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      workingHoursPercentage: [1.0, [Validators.required]],
      role: [''],
    });
  }

  ngOnInit(): void {
    this.resetForm();
    this.loadRoles();
    this.loggedInUser = this.authorizationService.getCurrentUser();
  }

  loadRoles(): void {
    this.rolesService.getAllRoles()
      .subscribe(fetchedRoles => {
        this.roles = fetchedRoles;
      });
  }

  createUser() {
    this.submitted = true;
    if (this.employeeForm.valid) {
      console.log('Creating employee');
      this.newUser = this.employeeForm.value;
      this.newUser.roles = ['employee']
      this.newUser.team = this.loggedInUser.team
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
      role: '',
      workingHoursPercentage: [1.0, [Validators.required, Validators.min(0)]],
      currentOverTime: null,
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

import {Component, OnInit} from '@angular/core';
import {RulesService} from '../../services/rules.service';
import {RolesService} from '../../services/roles.service';
import {RoleRules, Rule} from '../../interfaces/rule';
import {Role} from '../../interfaces/role';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-rules',
  templateUrl: './rules.component.html',
  styleUrls: ['./rules.component.scss'],
})
export class RulesComponent implements OnInit {
  loading = true;
  teamConstraints: Rule[] = [
    {name: 'workingHours', label: $localize`working hours`, value: null},
    {name: 'maxWeeklyHours', label: $localize`maximum weekly hours`, value: null},
    {name: 'maxConsecutiveShifts', label: $localize`maximum consecutive shifts`, value: null},
    {name: 'daytimeRequiredPeople', label: $localize`daytime required people`, value: null},
    {name: 'nighttimeRequiredPeople', label: $localize`nighttime required people`, value: null}
  ];
  showRulesEditCard = true;
  roles: Role[] = [];
  roleRules: RoleRules[] = [];
  selectedRoleRules?: RoleRules;
  selectedRule?: Rule;
  formMode: 'create' | 'edit' | 'details' = 'details';
  formTitle = '';
  formAction = '';
  submitted = false;

  constructor(
    private rulesService: RulesService,
    private rolesService: RolesService,
    private messageService: MessageService
  ) {
  }

  ngOnInit(): void {
    this.loadRules();
    this.loadRoleRules();
  }

  loadRules(): void {
    this.rulesService.getAllRulesFromTeam().subscribe((fetchedRules) => {
      this.teamConstraints[0].value = fetchedRules.workingHours;
      this.teamConstraints[1].value = fetchedRules.maxWeeklyHours;
      this.teamConstraints[2].value = fetchedRules.maxConsecutiveShifts;
      this.teamConstraints[3].value = fetchedRules.daytimeRequiredPeople;
      this.teamConstraints[4].value = fetchedRules.nighttimeRequiredPeople;
    });
  }

  loadRoleRules(): void {
    this.rolesService.getAllRolesFromTeam().subscribe((fetchedRoles) => {
      this.roles = fetchedRoles;
    });

    this.rulesService.getAllRoleRulesFromTeam().subscribe((fetchedRoleRules) => {
      this.roleRules = fetchedRoleRules;
    })
    /*

        this.rolesService.getAllRolesFromTeam().subscribe((fetchedRoles) => {
          this.roles = fetchedRoles;
          this.loading = false;

          for (const role of this.roles) {
            this.roleRules.push({role: role,
              daytimeRequiredPeople: 0, nighttimeRequiredPeople: 0,
              allowedFlexitimeMonthly: 0, allowedFlexitimeTotal: 0})
          }
        });*/
  }

  selectRoleRule(role: RoleRules) {
    /*    this.rulesService.getRulesFromRole(role.id!).subscribe((fetchedRules) => {
          this.selectedRoleRules = fetchedRules;
        })*/
    this.showRulesEditCard = false;
    this.selectedRoleRules = role
    this.formMode = 'details';
    this.selectedRule = {name: '', label: '', value: 0,}; // Initialize selectedRule to avoid errors
  }

  selectRule(rule: Rule) {
    this.showRulesEditCard = true;
    this.selectedRule = rule;
    this.formMode = 'details';
  }

  editRule() {
    this.formMode = 'edit';
  }

  getFormTitle(): string {
    /*   if (this.formMode === 'create') {
         this.formTitle = 'Create Rule';
         this.formAction = 'Create';
       } else */
    if (this.formMode === 'edit') {
      if (this.showRulesEditCard) {
        this.formTitle = 'Edit Rule';
      } else {
        this.formTitle = 'Edit Rules for Roles';
      }
      this.formAction = 'Save';
    } else {
      if (this.showRulesEditCard) {
        //this.formTitle = 'Edit Rule';
        this.formTitle = 'Rule Details';
      } else {
        this.formTitle = 'Role Rules Details';
      }
      this.formAction = 'Edit';
    }
    return this.formTitle;
  }

  createOrUpdateRule() {
    this.submitted = true;
    if (this.selectedRule && this.selectedRule.value !== null) {
      this.updateRule();
    } else {
      this.messageService.add({severity: 'warn', summary: 'Validation Failed', detail: 'Please read the warnings.'});
    }
  }

  updateRule() {
    if (this.showRulesEditCard) {
      this.teamConstraints.find(x => x.name == this.selectedRule!.name)!.value = this.selectedRule!.value;
      this.rulesService.saveRules(this.teamConstraints).subscribe();
    } else {
      this.rulesService.updateRoleRule(this.selectedRoleRules!).subscribe({
        next: () => {
          this.messageService.add({severity: 'success', summary: 'Successfully Updated Rules for Role'});
        },
        error: (error) => {
          console.error('Error updating rule:', error);
          this.messageService.add({severity: 'error', summary: 'Updating Rule Failed', detail: error.error});
        },
      })
    }
  }

  cancelEditing() {
    this.resetForm();
    this.formMode = 'details';
  }

  resetForm() {
    this.submitted = false;
    this.selectedRule = undefined;
  }

  handleKeydown(event: KeyboardEvent, item: Rule | RoleRules, type: string): void {
    if (event.key === 'Enter') {
      if (type === 'team') {
        this.selectRule(item as Rule);
      } else {
        this.selectRoleRule(item as RoleRules);
      }
    }
  }

  getNameOfRole(role: RoleRules) {
    return this.roles.find(x => {
      return x.id == role.roleId
    })?.name;
  }
}

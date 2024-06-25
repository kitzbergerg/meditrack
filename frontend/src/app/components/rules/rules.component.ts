import { Component, OnInit } from '@angular/core';
import { RulesService } from '../../services/rules.service';
import { RolesService } from '../../services/roles.service';
import { RoleRules, Rule } from '../../interfaces/rule';
import { Role } from '../../interfaces/role';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-rules',
  templateUrl: './rules.component.html',
  styleUrls: ['./rules.component.scss'],
})
export class RulesComponent implements OnInit {
  loading = true;
  teamConstraints: Rule[] = [
    { name: 'daytimeRequiredPeople', label: $localize`daytime required people`,
      description: $localize`number of required employees during the day`, value: null },
    { name: 'nighttimeRequiredPeople', label: $localize`nighttime required people`,
      description: $localize`number of required employees during the night`, value: null }
  ];
  showRulesEditCard = true;
  roles: Role[] = [];
  roleRules: RoleRules[] = [];
  selectedRoleRules?: RoleRules;
  selectedRule?: Rule;
  selectedRuleBackup?: Rule;
  selectedRoleRulesBackup?: RoleRules;
  formMode: 'create' | 'edit' | 'details' = 'details';
  formTitle = '';
  formAction = '';
  submitted = false;

  constructor(
    private rulesService: RulesService,
    private rolesService: RolesService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.loadRules();
    this.loadRoleRules();
  }

  loadRules(): void {
    this.rulesService.getAllRulesFromTeam().subscribe((fetchedRules) => {
      this.teamConstraints[0].value = fetchedRules.daytimeRequiredPeople;
      this.teamConstraints[1].value = fetchedRules.nighttimeRequiredPeople;
    });
  }

  loadRoleRules(): void {
    this.rolesService.getAllRolesFromTeam().subscribe((fetchedRoles) => {
      this.roles = fetchedRoles;

      this.rulesService.getAllRoleRulesFromTeam().subscribe((fetchedRoleRules) => {
        this.roleRules = fetchedRoleRules.sort((a, b) => {
          const roleA = this.getNameOfRole(a) || '';
          const roleB = this.getNameOfRole(b) || '';
          return roleA.localeCompare(roleB);
        });
      });
    });
  }

  selectRoleRule(role: RoleRules) {
    this.showRulesEditCard = false;
    this.selectedRoleRules = { ...role }; // Shallow copy for editing
    this.selectedRoleRulesBackup = { ...role }; // Backup original values
    this.formMode = 'details';
    this.selectedRule = { name: '', label: '', description: '', value: 0 }; // Initialize selectedRule to avoid errors
  }

  selectRule(rule: Rule) {
    this.showRulesEditCard = true;
    this.selectedRule = { ...rule }; // Shallow copy for editing
    this.selectedRuleBackup = { ...rule }; // Backup original values
    this.formMode = 'details';
  }

  editRule() {
    this.formMode = 'edit';
  }

  getFormTitle(): string {
    if (this.formMode === 'edit') {
      if (this.showRulesEditCard) {
        this.formTitle = $localize`@@rules.component.edit-rule:Edit Rule`;
      } else {
        this.formTitle = $localize`@@rules.component.edit-rules-for-roles:Edit Rules for Role` + ' ' +
          this.roles.find(x => x.id == this.selectedRoleRules?.roleId)?.name;
      }
      this.formAction = $localize`@@rules.component.save:Save`;
    } else {
      if (this.showRulesEditCard) {
        this.formTitle = $localize`@@rules.component.rule-details:Rule Details`;
      } else {
        this.formTitle = $localize`@@rules.component.rule-details-for-role:Rule Details For Role` + ' ' +
          this.roles.find(x => x.id == this.selectedRoleRules?.roleId)?.name;
      }
      this.formAction = $localize`@@rules.component.edit:Edit`;
    }
    return this.formTitle;
  }

  createOrUpdateRule() {
    this.submitted = true;
    if (this.selectedRule && this.selectedRule.value !== null) {
      this.updateRule();
    } else {
      this.messageService.add({
        severity: 'warn',
        summary: $localize`@@rules.component.validation-failed:Validation Failed`,
        detail: $localize`@@rules.component.please-read-the-warnings:Please read the warnings.` });
    }
  }

  updateRule() {
    if (this.showRulesEditCard) {
      this.teamConstraints.find(x => x.name === this.selectedRule!.name)!.value = this.selectedRule!.value;
      this.rulesService.saveRules(this.teamConstraints).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success',
            summary: $localize`@@rules.component.successfully-updated-rule:Successfully Updated Rule` });
          this.formMode = 'details'; // Revert to non-edit mode
          this.selectedRuleBackup = { ...this.selectedRule! };
        },
        error: (error) => {
          console.error('Error updating rule:', error);
          this.messageService.add({
            severity: 'error',
            summary: $localize`@@rules.component.updating-rule-failed:Updating Rule Failed`,
            detail: JSON.stringify(error.error) });
        }
      });
    } else {
      if (this.selectedRoleRules &&
        this.selectedRoleRules.daytimeRequiredPeople !== null && !isNaN(this.selectedRoleRules.daytimeRequiredPeople) &&
        this.selectedRoleRules.nighttimeRequiredPeople !== null && !isNaN(this.selectedRoleRules.nighttimeRequiredPeople) &&
        this.selectedRoleRules.allowedFlextimeTotal !== null && !isNaN(this.selectedRoleRules.allowedFlextimeTotal) &&
        this.selectedRoleRules.allowedFlextimePerMonth !== null && !isNaN(this.selectedRoleRules.allowedFlextimePerMonth)) {
        this.rulesService.updateRoleRule(this.selectedRoleRules!).subscribe({
          next: () => {
            this.messageService.add({severity: 'success',
              summary: $localize`@@rules.component.successfully-updated-rules-for-role:Successfully Updated Rules for Role`});
            this.formMode = 'details'; // Revert to non-edit mode
            // Update backup values after successful save
            this.selectedRoleRulesBackup = { ...this.selectedRoleRules! };
          },
          error: (error) => {
            console.error('Error updating rule:', error);
            this.messageService.add({
              severity: 'error',
              summary: $localize`@@rules.component.updating-rule-failed:Updating rules for role failed`,
              detail: JSON.stringify(error.error) });
          }
        });
      } else {
        this.messageService.add({severity: 'warn',
          summary: $localize`@@rules.component.validation-failed:Validation Failed`,
          detail: $localize`@@rules.component.please-read-the-warnings:Please read the warnings.` });
      }
    }
  }

  cancelEditing() {
    if (this.showRulesEditCard && this.selectedRuleBackup) {
      this.selectedRule = { ...this.selectedRuleBackup };
    } else if (!this.showRulesEditCard && this.selectedRoleRulesBackup) {
      this.selectedRoleRules = { ...this.selectedRoleRulesBackup };
    }
    this.formMode = 'details';
    this.submitted = false;
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
    return this.roles.find(x => x.id === role.roleId)?.name;
  }
}

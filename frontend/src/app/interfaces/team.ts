export interface Team {
  id?: string;
  name: string;
  workingHours?: null | number;
  users?: string[];
  roles?: string;
  hardConstraints?: any;
  monthlyPlans?: [];
  shiftTypes?: [];
}

export interface Team {
  id?: string;
  name: string;
  workingHours?: null | number;
  users?: string[];
  roles?: string;
  hardConstraints?: null | any;
  monthlyPlans?: null | any;
  shiftTypes?: null | any;
}

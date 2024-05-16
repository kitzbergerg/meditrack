export interface Team {
  id?: string;
  name: string;
  workingHours?: null | number;
  users?: string[];
  hardConstraints?: null | any;
  monthlyPlans?: null | any;
  shiftTypes?: null | any;
}

export interface Role {
  id?: number,
  name: string,
  users?: number[],
  color: string;
  abbreviation: string;
  team?: string;
}

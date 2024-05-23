export interface Role {
  id?: number,
  name: string,
  users?: number[],
  color: string;
  abbreviation: string;
}

export interface RoleCreate {
  name: string;
  color: string;
  abbreviation: string;
}

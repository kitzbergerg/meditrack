export interface Role {
  id?: number,
  name: string,
  users?: number[]
}

export interface RoleCreate {
  name: string
}

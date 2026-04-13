export interface UserSummary {
  id: number
  name: string
  email: string
}

export interface LoginResponse {
  accessToken: string
  user: UserSummary
}

export interface RegisterResponse {
  userId: number
  message: string
}

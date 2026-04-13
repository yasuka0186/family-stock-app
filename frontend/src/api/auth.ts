import { http } from './http'
import type { LoginResponse, RegisterResponse } from '../types/auth'

export const authApi = {
  register: (payload: { name: string; email: string; password: string }) =>
    http.post<RegisterResponse>('/api/auth/register', payload),
  login: (payload: { email: string; password: string }) =>
    http.post<LoginResponse>('/api/auth/login', payload),
  me: () => http.get('/api/auth/me')
}

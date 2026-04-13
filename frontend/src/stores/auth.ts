import { defineStore } from 'pinia'
import { authApi } from '../api/auth'
import type { UserSummary } from '../types/auth'

/**
 * 認証状態管理ストア。
 * localStorageを使う理由は、MVPで最小実装かつページ再読み込み耐性を確保するため。
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('accessToken') || '',
    user: JSON.parse(localStorage.getItem('authUser') || 'null') as UserSummary | null
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token)
  },
  actions: {
    setAuth(token: string, user: UserSummary) {
      this.token = token
      this.user = user
      localStorage.setItem('accessToken', token)
      localStorage.setItem('authUser', JSON.stringify(user))
    },
    clearAuth() {
      this.token = ''
      this.user = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('authUser')
    },
    async login(email: string, password: string) {
      const { data } = await authApi.login({ email, password })
      this.setAuth(data.accessToken, data.user)
    },
    async register(name: string, email: string, password: string) {
      await authApi.register({ name, email, password })
    }
  }
})

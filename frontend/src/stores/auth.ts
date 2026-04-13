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

    /**
     * 認証情報保存。
     * localStorageへ退避して、リロード後もログインを維持する。
     */

    setAuth(token: string, user: UserSummary) {
      this.token = token
      this.user = user
      localStorage.setItem('accessToken', token)
      localStorage.setItem('authUser', JSON.stringify(user))
    },

    /**
     * ログアウト相当の初期化処理。
     * 401時の共通ハンドリングとも同じ保存先を削除して整合を保つ。
     */

    clearAuth() {
      this.token = ''
      this.user = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('authUser')
    },

    /**
     * ログインAPI呼び出し。
     * 以降のAPI利用で必要なトークンを先に永続化する。
     */

    async login(email: string, password: string) {
      const { data } = await authApi.login({ email, password })
      this.setAuth(data.accessToken, data.user)
    },

    /**
     * 登録API呼び出し。
     * MVP方針に合わせ、登録後の画面遷移はView側で制御する。
     */

    async register(name: string, email: string, password: string) {
      await authApi.register({ name, email, password })
    }
  }
})

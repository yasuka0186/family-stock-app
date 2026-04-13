import axios from 'axios'

/**
 * 共通APIクライアント。
 * localStorageトークンを自動付与して、画面側での認証ヘッダー重複実装を避ける。
 */
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 認証切れ時は不正状態を残さずログインへ戻し、保護画面アクセスを防ぐ。
      localStorage.removeItem('accessToken')
      localStorage.removeItem('authUser')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

import axios from 'axios'

/**
 * APIエラー文言の取り出し共通関数。
 * 画面ごとに分岐を重複させないため、最低限の形式だけをここで吸収する。
 */
export function getErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message
    if (typeof message === 'string' && message.length > 0) {
      return message
    }
  }
  return fallback
}

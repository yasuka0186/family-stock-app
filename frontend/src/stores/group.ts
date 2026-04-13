import { defineStore } from 'pinia'
import { groupApi } from '../api/group'
import type { GroupSummary } from '../types/group'

/**
 * グループ所属状態管理ストア。
 * 所属状態で遷移先を変えるため、guard前提の情報を保持する。
 */
export const useGroupStore = defineStore('group', {
  state: () => ({
    group: null as GroupSummary | null,
    loaded: false
  }),
  getters: {
    hasGroup: (state) => Boolean(state.group)
  },
  actions: {
<<<<<<< ours
=======
    /**
     * 所属グループ取得。
     * route guardからも利用し、遷移条件の判定元を一本化する。
     */
>>>>>>> theirs
    async fetchMyGroup() {
      const { data } = await groupApi.me()
      this.group = data.group
      this.loaded = true
      return data
    },
<<<<<<< ours
=======
    /**
     * グループ新規作成。
     * 作成成功時点で所属済みになるため、store状態を即時更新する。
     */
>>>>>>> theirs
    async createGroup(name: string) {
      const { data } = await groupApi.create({ name })
      this.group = data
      this.loaded = true
    },
<<<<<<< ours
=======
    /**
     * 招待コード参加。
     * 参加後は在庫画面へ進めるよう、所属状態を保存する。
     */
>>>>>>> theirs
    async joinGroup(inviteCode: string) {
      const { data } = await groupApi.join({ inviteCode })
      this.group = data
      this.loaded = true
    },
<<<<<<< ours
=======
    /**
     * グループ状態の初期化。
     * ログアウト時や所属判定失敗時の安全な戻し先として使う。
     */
>>>>>>> theirs
    reset() {
      this.group = null
      this.loaded = false
    }
  }
})

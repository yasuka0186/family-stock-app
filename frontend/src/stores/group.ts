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
    async fetchMyGroup() {
      const { data } = await groupApi.me()
      this.group = data.group
      this.loaded = true
      return data
    },
    async createGroup(name: string) {
      const { data } = await groupApi.create({ name })
      this.group = data
      this.loaded = true
    },
    async joinGroup(inviteCode: string) {
      const { data } = await groupApi.join({ inviteCode })
      this.group = data
      this.loaded = true
    },
    reset() {
      this.group = null
      this.loaded = false
    }
  }
})

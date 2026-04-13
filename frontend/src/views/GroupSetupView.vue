<template>
  <main class="page">
    <div class="card">
      <h2>家族グループ作成</h2>
      <label>グループ名</label>
      <input v-model="groupName" />
      <button style="margin-top: 10px" @click="createGroup">作成する</button>
    </div>

    <div class="card">
      <h2>招待コードで参加</h2>
      <label>招待コード</label>
      <input v-model="inviteCode" />
      <button style="margin-top: 10px" @click="joinGroup">参加する</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGroupStore } from '../stores/group'

/**
 * グループ作成/参加画面。
 * グループ未所属ユーザー向け画面として機能し、所属済みなら在庫画面へ誘導する。
 */
const router = useRouter()
const groupStore = useGroupStore()
const groupName = ref('')
const inviteCode = ref('')
const error = ref('')

onMounted(async () => {
  try {
    await groupStore.fetchMyGroup()
    if (groupStore.hasGroup) {
      router.push('/stocks')
    }
  } catch {
    // 未所属想定のため無視
  }
})

async function createGroup() {
  error.value = ''
  try {
    await groupStore.createGroup(groupName.value)
    router.push('/stocks')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'グループ作成に失敗しました'
  }
}

async function joinGroup() {
  error.value = ''
  try {
    await groupStore.joinGroup(inviteCode.value)
    router.push('/stocks')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'グループ参加に失敗しました'
  }
}
</script>

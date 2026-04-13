<template>
  <div>
    <nav class="nav">
      <router-link to="/stocks">在庫</router-link>
      <router-link to="/shopping-list">買い物リスト</router-link>
      <button class="secondary" @click="logout">ログアウト</button>
    </nav>
    <main class="page">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGroupStore } from '../stores/group'

/**
 * 認証済み画面共通レイアウト。
 * ログアウト操作を一箇所に寄せて画面ごとの重複を避ける。
 */
const router = useRouter()
const authStore = useAuthStore()
const groupStore = useGroupStore()

function logout() {
  authStore.clearAuth()
  groupStore.reset()
  router.push('/login')
}
</script>

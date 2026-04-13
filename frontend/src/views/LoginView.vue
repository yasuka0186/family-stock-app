<template>
  <main class="page">
    <div class="card">
      <h2>ログイン</h2>
      <label>メール</label>
      <input v-model="email" type="email" />
      <label>パスワード</label>
      <input v-model="password" type="password" />
      <div class="row" style="margin-top: 12px">
        <button @click="submit">ログイン</button>
        <router-link to="/register">新規登録</router-link>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGroupStore } from '../stores/group'

/**
 * ログイン画面。
 * 認証成功後にグループ所属状態を判定し、次画面を分岐する。
 */
const router = useRouter()
const authStore = useAuthStore()
const groupStore = useGroupStore()

const email = ref('')
const password = ref('')
const error = ref('')

async function submit() {
  error.value = ''
  try {
    await authStore.login(email.value, password.value)
    await groupStore.fetchMyGroup()
    router.push(groupStore.hasGroup ? '/stocks' : '/group-setup')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'ログインに失敗しました'
  }
}
</script>

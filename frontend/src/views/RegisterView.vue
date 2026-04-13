<template>
  <main class="page">
    <div class="card">
      <h2>ユーザー登録</h2>
      <label>名前</label>
      <input v-model="name" />
      <label>メール</label>
      <input v-model="email" type="email" />
      <label>パスワード</label>
      <input v-model="password" type="password" />
      <div class="row" style="margin-top: 12px">
        <button @click="submit">登録</button>
        <router-link to="/login">ログインへ戻る</router-link>
      </div>
      <p v-if="success" class="success">{{ success }}</p>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
<<<<<<< ours
=======
import { getErrorMessage } from '../utils/error'
>>>>>>> theirs

/**
 * ユーザー登録画面。
 * MVPでは登録後に自動ログインせず、ログイン導線へ戻すシンプル運用にする。
 */
const router = useRouter()
const authStore = useAuthStore()

const name = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const success = ref('')

async function submit() {
  error.value = ''
  success.value = ''
  try {
<<<<<<< ours
    await authStore.register(name.value, email.value, password.value)
    success.value = '登録しました。ログインしてください。'
    setTimeout(() => router.push('/login'), 500)
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '登録に失敗しました'
=======
    // MVPでは登録とログインを分離し、失敗時の切り分けを簡単にする。
    await authStore.register(name.value, email.value, password.value)
    success.value = '登録しました。ログインしてください。'
    setTimeout(() => router.push('/login'), 500)
  } catch (e: unknown) {
    error.value = getErrorMessage(e, '登録に失敗しました')
>>>>>>> theirs
  }
}
</script>

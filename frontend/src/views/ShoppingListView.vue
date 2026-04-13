<template>
  <AppLayout>
    <div class="row" style="margin-bottom: 12px">
      <h2 style="margin: 0">買い物リスト</h2>
      <select v-model="statusFilter" @change="loadItems">
        <option value="">PENDING(既定)</option>
        <option value="PENDING">PENDING</option>
        <option value="BOUGHT">BOUGHT</option>
        <option value="SKIPPED">SKIPPED</option>
      </select>
    </div>

    <div class="card">
      <h3>手動追加</h3>
      <label>既存在庫から選択(任意)</label>
      <select v-model="createForm.stockItemId">
        <option :value="''">自由入力で追加</option>
        <option v-for="s in stockItems" :key="s.id" :value="String(s.id)">{{ s.name }} ({{ s.unit }})</option>
      </select>
      <label>名前（自由入力時必須）</label>
      <input v-model="createForm.name" />
      <label>単位（自由入力時必須）</label>
      <input v-model="createForm.unit" />
      <label>メモ(任意)</label>
      <input v-model="createForm.note" />
      <button style="margin-top: 10px" @click="createItem">追加する</button>
      <p v-if="createMessage" class="success">{{ createMessage }}</p>
    </div>

    <div v-if="items.length === 0" class="card">買い物リストは空です。</div>

    <div v-for="item in items" :key="item.id" class="card">
      <div class="row">
        <strong>{{ item.name }}</strong>
        <span>{{ item.unit }}</span>
        <span>{{ item.status }}</span>
        <span>{{ item.sourceType }}</span>
      </div>
      <div class="row" style="margin-top: 8px">
        <button @click="updateStatus(item.id, 'BOUGHT')">BOUGHT</button>
        <button class="secondary" @click="updateStatus(item.id, 'SKIPPED')">SKIPPED</button>
        <button class="secondary" @click="updateStatus(item.id, 'PENDING')">PENDINGへ戻す</button>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
  </AppLayout>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import AppLayout from '../layouts/AppLayout.vue'
import { shoppingApi } from '../api/shopping'
import { stockApi } from '../api/stock'
import type { ShoppingListItem, ShoppingListStatus } from '../types/shopping'
import type { StockItem } from '../types/stock'
<<<<<<< ours
=======
import { getErrorMessage } from '../utils/error'
>>>>>>> theirs

/**
 * 買い物リスト画面。
 * 一覧確認・手動追加・状態更新を同一画面で完結させ、MVP操作性を優先する。
 */
const items = ref<ShoppingListItem[]>([])
const stockItems = ref<StockItem[]>([])
const statusFilter = ref('')
const error = ref('')
const createMessage = ref('')

const createForm = reactive({
  stockItemId: '',
  name: '',
  unit: '',
  note: ''
})

onMounted(async () => {
  await Promise.all([loadItems(), loadStockItems()])
})

<<<<<<< ours
=======
/**
 * 買い物リスト読み込み。
 * 更新直後の表示ずれを防ぐため、状態変更後もこの関数で再取得する。
 */
>>>>>>> theirs
async function loadItems() {
  error.value = ''
  try {
    const status = (statusFilter.value || undefined) as ShoppingListStatus | undefined
    const { data } = await shoppingApi.list(status)
    items.value = data
<<<<<<< ours
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '買い物リスト取得に失敗しました'
=======
  } catch (e: unknown) {
    error.value = getErrorMessage(e, '買い物リスト取得に失敗しました')
>>>>>>> theirs
  }
}

async function loadStockItems() {
  try {
    const { data } = await stockApi.list(false)
    stockItems.value = data
  } catch {
    // 在庫取得失敗時も買い物リスト本体は表示継続
  }
}

<<<<<<< ours
=======
/**
 * 手動追加処理。
 * 在庫紐付けあり/なしの両方を同一フォームで扱い、MVP導線を単純化する。
 */
>>>>>>> theirs
async function createItem() {
  error.value = ''
  createMessage.value = ''

  try {
    const payload: { stockItemId?: number; name?: string; unit?: string; note?: string } = {
      note: createForm.note || undefined
    }

    if (createForm.stockItemId) {
      payload.stockItemId = Number(createForm.stockItemId)
    } else {
      payload.name = createForm.name
      payload.unit = createForm.unit
    }

    const { data } = await shoppingApi.create(payload)
    createMessage.value = data.created ? '追加しました' : 'すでにPENDINGが存在するため既存を表示しました'
    await loadItems()
<<<<<<< ours
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '手動追加に失敗しました'
  }
}

=======
  } catch (e: unknown) {
    error.value = getErrorMessage(e, '手動追加に失敗しました')
  }
}

/**
 * 状態更新処理。
 * 画面側は操作結果の反映までを責務とし、業務整合はバックエンドに委譲する。
 */
>>>>>>> theirs
async function updateStatus(id: number, status: ShoppingListStatus) {
  error.value = ''
  try {
    await shoppingApi.updateStatus(id, { status })
    await loadItems()
<<<<<<< ours
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '状態更新に失敗しました'
=======
  } catch (e: unknown) {
    error.value = getErrorMessage(e, '状態更新に失敗しました')
>>>>>>> theirs
  }
}
</script>

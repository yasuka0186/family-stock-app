<template>
  <AppLayout>
    <div class="row" style="margin-bottom: 12px">
      <h2 style="margin: 0">在庫一覧</h2>
      <router-link to="/stocks/new">＋ 新規作成</router-link>
      <router-link to="/shopping-list">買い物リスト</router-link>
      <label style="margin-left: auto">
        <input type="checkbox" v-model="lowStockOnly" @change="load" /> 低在庫のみ
      </label>
    </div>

    <div v-if="items.length === 0" class="card">在庫アイテムがありません。</div>

    <div v-for="item in items" :key="item.id" class="card" :class="{ 'low-stock': item.lowStock }">
      <div class="row">
        <strong>{{ item.name }}</strong>
        <span>{{ item.currentStock }} {{ item.unit }}</span>
        <span>最低: {{ item.minimumStock }}</span>
        <span v-if="item.lowStock" class="error">不足</span>
        <router-link :to="`/stocks/${item.id}/edit`">編集</router-link>
        <button class="danger" @click="remove(item.id)">削除</button>
      </div>
      <StockQuantityUpdater :item="item" @updated="load" />
    </div>

    <p v-if="error" class="error">{{ error }}</p>
  </AppLayout>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AppLayout from '../layouts/AppLayout.vue'
import { stockApi } from '../api/stock'
import type { StockItem } from '../types/stock'
import StockQuantityUpdater from '../components/StockQuantityUpdater.vue'
<<<<<<< ours
=======
import { getErrorMessage } from '../utils/error'
>>>>>>> theirs

/**
 * 在庫一覧画面。
 * 在庫確認・編集・数量更新への導線を集約して日常運用の操作回数を減らす。
 */
const items = ref<StockItem[]>([])
const lowStockOnly = ref(false)
const error = ref('')

onMounted(load)

<<<<<<< ours
=======
/**
 * 一覧再取得処理。
 * 在庫更新・削除後にサーバー基準の状態へ揃えるため、都度再フェッチする。
 */
>>>>>>> theirs
async function load() {
  error.value = ''
  try {
    const { data } = await stockApi.list(lowStockOnly.value)
    items.value = data
<<<<<<< ours
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '在庫取得に失敗しました'
  }
}

=======
  } catch (e: unknown) {
    error.value = getErrorMessage(e, '在庫取得に失敗しました')
  }
}

/**
 * 在庫削除処理。
 * 削除後は一覧を再取得し、数量更新など他操作と表示整合を保つ。
 */
>>>>>>> theirs
async function remove(id: number) {
  if (!confirm('削除しますか？')) return
  try {
    await stockApi.remove(id)
    await load()
<<<<<<< ours
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '削除に失敗しました'
=======
  } catch (e: unknown) {
    error.value = getErrorMessage(e, '削除に失敗しました')
>>>>>>> theirs
  }
}
</script>

<template>
  <div class="card">
    <div class="row">
      <select v-model="mode">
        <option value="SET">SET</option>
        <option value="ADD">ADD</option>
        <option value="SUBTRACT">SUBTRACT</option>
      </select>
      <input v-model.number="quantity" type="number" min="0" placeholder="数量" />
      <select v-model="reason">
        <option value="">理由(任意)</option>
        <option value="CONSUME">CONSUME</option>
        <option value="PURCHASE">PURCHASE</option>
        <option value="ADJUST">ADJUST</option>
      </select>
      <button @click="submit">数量更新</button>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { stockApi } from '../api/stock'
import type { StockItem, StockUpdateMode, StockUpdateReason } from '../types/stock'
<<<<<<< ours
=======
import { getErrorMessage } from '../utils/error'
>>>>>>> theirs

/**
 * 在庫数更新UI。
 * 在庫一覧から直接更新できるようにし、日常操作の手数を減らす。
 */
const props = defineProps<{ item: StockItem }>()
const emit = defineEmits<{ updated: [] }>()

const mode = ref<StockUpdateMode>('SET')
const quantity = ref<number>(props.item.currentStock)
const reason = ref<StockUpdateReason | ''>('')
const error = ref('')

async function submit() {
  error.value = ''
  if (quantity.value < 0) {
    error.value = '数量は0以上で入力してください'
    return
  }

  try {
<<<<<<< ours
=======
    // サーバー側の自動買い物リスト連携ロジックを活かすため、更新APIを必ず経由する。
>>>>>>> theirs
    await stockApi.updateStock(props.item.id, {
      mode: mode.value,
      quantity: quantity.value,
      reason: reason.value || undefined
    })
<<<<<<< ours
    emit('updated')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '在庫更新に失敗しました'
=======
    // 親で一覧再取得して表示整合を取るため、更新イベントのみ通知する。
    emit('updated')
  } catch (e: unknown) {
    error.value = getErrorMessage(e, '在庫更新に失敗しました')
>>>>>>> theirs
  }
}
</script>

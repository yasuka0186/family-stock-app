<template>
  <AppLayout>
    <div class="card">
      <h2>{{ isEdit ? '在庫編集' : '在庫作成' }}</h2>

      <label>名前</label>
      <input v-model="form.name" />
      <label>カテゴリ(任意)</label>
      <input v-model="form.category" />
      <label>単位</label>
      <input v-model="form.unit" />
      <label>現在在庫</label>
      <input v-model.number="form.currentStock" type="number" min="0" />
      <label>最低在庫</label>
      <input v-model.number="form.minimumStock" type="number" min="0" />

      <div class="row" style="margin-top: 12px">
        <button @click="submit">保存</button>
        <router-link to="/stocks">一覧へ戻る</router-link>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppLayout from '../layouts/AppLayout.vue'
import { stockApi } from '../api/stock'
import type { StockItemForm } from '../types/stock'

/**
 * 在庫作成/編集画面。
 * 同一フォームで作成と編集を扱い、MVPの保守コストを下げる。
 */
const route = useRoute()
const router = useRouter()
const error = ref('')

const form = reactive<StockItemForm>({
  name: '',
  category: '',
  unit: '個',
  currentStock: 0,
  minimumStock: 0
})

const isEdit = computed(() => Boolean(route.params.id))

onMounted(async () => {
  if (!isEdit.value) return
  try {
    const { data } = await stockApi.get(Number(route.params.id))
    form.name = data.name
    form.category = data.category || ''
    form.unit = data.unit
    form.currentStock = data.currentStock
    form.minimumStock = data.minimumStock
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '在庫取得に失敗しました'
  }
})

async function submit() {
  error.value = ''
  if (!form.name || !form.unit || form.currentStock < 0 || form.minimumStock < 0) {
    error.value = '入力内容を確認してください'
    return
  }

  try {
    if (isEdit.value) {
      await stockApi.update(Number(route.params.id), form)
    } else {
      await stockApi.create(form)
    }
    router.push('/stocks')
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? '保存に失敗しました'
  }
}
</script>

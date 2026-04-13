import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGroupStore } from '../stores/group'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import GroupSetupView from '../views/GroupSetupView.vue'
import StockListView from '../views/StockListView.vue'
import StockItemFormView from '../views/StockItemFormView.vue'
import ShoppingListView from '../views/ShoppingListView.vue'

const routes = [
  { path: '/', redirect: '/stocks' },
  { path: '/login', component: LoginView, meta: { guestOnly: true } },
  { path: '/register', component: RegisterView, meta: { guestOnly: true } },
  { path: '/group-setup', component: GroupSetupView, meta: { requiresAuth: true, grouplessOnly: true } },
  { path: '/stocks', component: StockListView, meta: { requiresAuth: true, requiresGroup: true } },
  { path: '/stocks/new', component: StockItemFormView, meta: { requiresAuth: true, requiresGroup: true } },
  { path: '/stocks/:id/edit', component: StockItemFormView, meta: { requiresAuth: true, requiresGroup: true } },
  { path: '/shopping-list', component: ShoppingListView, meta: { requiresAuth: true, requiresGroup: true } }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * ルートガード。
 * 認証状態とグループ所属状態で遷移先を制御し、無限リダイレクトを避ける。
 */
router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  const groupStore = useGroupStore()

  if (to.meta.guestOnly && authStore.isAuthenticated) {
    if (!groupStore.loaded) {
      await safeFetchGroup(groupStore)
    }
    return groupStore.hasGroup ? '/stocks' : '/group-setup'
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return '/login'
  }

  if (to.meta.requiresAuth && authStore.isAuthenticated && !groupStore.loaded) {
    await safeFetchGroup(groupStore)
  }

  if (to.meta.grouplessOnly && groupStore.hasGroup) {
    return '/stocks'
  }

  if (to.meta.requiresGroup && !groupStore.hasGroup) {
    return '/group-setup'
  }

  return true
})

async function safeFetchGroup(groupStore: ReturnType<typeof useGroupStore>) {
  try {
    await groupStore.fetchMyGroup()
  } catch {
    groupStore.reset()
  }
}

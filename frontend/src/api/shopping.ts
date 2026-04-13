import { http } from './http'
import type { ShoppingListCreateResponse, ShoppingListItem, ShoppingListStatus } from '../types/shopping'

export const shoppingApi = {
  list: (status?: ShoppingListStatus) => http.get<ShoppingListItem[]>('/api/shopping-list-items', { params: { status } }),
  create: (payload: { stockItemId?: number; name?: string; unit?: string; note?: string }) =>
    http.post<ShoppingListCreateResponse>('/api/shopping-list-items', payload),
  updateStatus: (id: number, payload: { status: ShoppingListStatus }) =>
    http.patch<ShoppingListItem>(`/api/shopping-list-items/${id}/status`, payload)
}

import { http } from './http'
import type { StockItem, StockItemForm, StockUpdateMode, StockUpdateReason } from '../types/stock'

export const stockApi = {
  list: (lowStockOnly = false) => http.get<StockItem[]>('/api/stock-items', { params: { lowStockOnly } }),
  get: (id: number) => http.get<StockItem>(`/api/stock-items/${id}`),
  create: (payload: StockItemForm) => http.post<StockItem>('/api/stock-items', payload),
  update: (id: number, payload: StockItemForm) => http.put<StockItem>(`/api/stock-items/${id}`, payload),
  remove: (id: number) => http.delete(`/api/stock-items/${id}`),
  updateStock: (id: number, payload: { mode: StockUpdateMode; quantity: number; reason?: StockUpdateReason }) =>
    http.patch<StockItem>(`/api/stock-items/${id}/stock`, payload)
}

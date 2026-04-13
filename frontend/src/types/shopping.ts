export type ShoppingListStatus = 'PENDING' | 'BOUGHT' | 'SKIPPED'
export type ShoppingListSourceType = 'MANUAL' | 'AUTO_LOW_STOCK'

export interface ShoppingListItem {
  id: number
  stockItemId: number | null
  name: string
  unit: string
  status: ShoppingListStatus
  sourceType: ShoppingListSourceType
  note: string | null
  createdAt: string
}

export interface ShoppingListCreateResponse {
  created: boolean
  item: ShoppingListItem
}

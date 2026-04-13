export type StockUpdateMode = 'SET' | 'ADD' | 'SUBTRACT'
export type StockUpdateReason = 'CONSUME' | 'PURCHASE' | 'ADJUST'

export interface StockItem {
  id: number
  familyGroupId: number
  name: string
  category: string | null
  unit: string
  currentStock: number
  minimumStock: number
  lowStock: boolean
  createdByEmail: string | null
}

export interface StockItemForm {
  name: string
  category?: string
  unit: string
  currentStock: number
  minimumStock: number
}

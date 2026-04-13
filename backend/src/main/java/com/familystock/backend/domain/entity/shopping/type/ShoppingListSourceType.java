package com.familystock.backend.domain.entity.shopping.type;

/**
 * 買い物リスト項目の追加元を表す列挙型。
 * 自動追加と手動追加を区別し、将来の分析や履歴表示に備える。
 */
public enum ShoppingListSourceType {
    AUTO_LOW_STOCK,
    MANUAL
}

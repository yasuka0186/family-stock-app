package com.familystock.backend.domain.entity.shopping.type;

/**
 * 買い物リスト項目の状態を表す列挙型。
 * MVPでは状態遷移をシンプルに保ち、物理削除の代わりに状態更新を使う。
 */
public enum ShoppingListStatus {
    PENDING,
    BOUGHT,
    SKIPPED
}

package com.familystock.backend.dto.request.type;

/**
 * 在庫更新の理由を表す列挙型。
 * TODO: 在庫履歴テーブル導入時に、監査用途で永続化する。
 */
public enum StockUpdateReason {
    CONSUME,
    PURCHASE,
    ADJUST
}

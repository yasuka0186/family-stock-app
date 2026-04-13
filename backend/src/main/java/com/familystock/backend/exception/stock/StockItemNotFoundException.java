package com.familystock.backend.exception.stock;

/**
 * 在庫アイテムが見つからない場合の例外。
 * 他グループ境界でのアクセス時も同一エラーにして情報漏えいを抑える。
 */
public class StockItemNotFoundException extends RuntimeException {

    public StockItemNotFoundException(String message) {
        super(message);
    }
}

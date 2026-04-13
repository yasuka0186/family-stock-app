package com.familystock.backend.exception.stock;

/**
 * 同一グループで同名在庫が重複した場合の例外。
 */
public class DuplicateStockItemException extends RuntimeException {

    public DuplicateStockItemException(String message) {
        super(message);
    }
}

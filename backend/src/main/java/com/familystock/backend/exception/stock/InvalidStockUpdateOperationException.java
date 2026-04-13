package com.familystock.backend.exception.stock;

/**
 * 在庫数更新ルールに反する操作が指定された場合の例外。
 * 例: SUBTRACTで在庫が負数になるケース。
 */
public class InvalidStockUpdateOperationException extends RuntimeException {

    public InvalidStockUpdateOperationException(String message) {
        super(message);
    }
}

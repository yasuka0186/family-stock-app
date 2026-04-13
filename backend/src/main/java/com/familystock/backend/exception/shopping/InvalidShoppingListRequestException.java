package com.familystock.backend.exception.shopping;

/**
 * 買い物リスト入力が業務ルールを満たさない場合の例外。
 */
public class InvalidShoppingListRequestException extends RuntimeException {

    public InvalidShoppingListRequestException(String message) {
        super(message);
    }
}

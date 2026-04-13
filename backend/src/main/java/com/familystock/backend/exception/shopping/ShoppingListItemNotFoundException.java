package com.familystock.backend.exception.shopping;

/**
 * 買い物リスト項目が見つからない場合の例外。
 * 他グループアクセスも同一扱いにして情報漏えいを防ぐ。
 */
public class ShoppingListItemNotFoundException extends RuntimeException {

    public ShoppingListItemNotFoundException(String message) {
        super(message);
    }
}

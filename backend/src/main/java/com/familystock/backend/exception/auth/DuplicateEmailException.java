package com.familystock.backend.exception.auth;

/**
 * 既に登録済みのメールアドレスで登録要求が来た場合の例外。
 * フロント側では入力内容の修正促進に利用する。
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}

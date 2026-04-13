package com.familystock.backend.exception.auth;

/**
 * 認証情報が不正な場合の例外。
 * セキュリティ上、ユーザー存在有無の詳細は返さない。
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

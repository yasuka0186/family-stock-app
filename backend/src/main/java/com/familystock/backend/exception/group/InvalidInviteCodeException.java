package com.familystock.backend.exception.group;

/**
 * 招待コードに対応するグループが見つからない場合の例外。
 */
public class InvalidInviteCodeException extends RuntimeException {

    public InvalidInviteCodeException(String message) {
        super(message);
    }
}

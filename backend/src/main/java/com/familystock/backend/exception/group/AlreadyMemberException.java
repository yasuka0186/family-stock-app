package com.familystock.backend.exception.group;

/**
 * 同一グループへの二重参加要求を検出した場合の例外。
 */
public class AlreadyMemberException extends RuntimeException {

    public AlreadyMemberException(String message) {
        super(message);
    }
}

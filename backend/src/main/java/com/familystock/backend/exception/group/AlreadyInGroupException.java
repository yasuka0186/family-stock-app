package com.familystock.backend.exception.group;

/**
 * ユーザーが既にどこかのグループへ所属している場合の例外。
 * MVPでは1ユーザー1グループ制約違反として扱う。
 */
public class AlreadyInGroupException extends RuntimeException {

    public AlreadyInGroupException(String message) {
        super(message);
    }
}

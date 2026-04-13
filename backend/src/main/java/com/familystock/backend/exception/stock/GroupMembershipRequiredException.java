package com.familystock.backend.exception.stock;

/**
 * 在庫操作時にユーザーがグループ未所属の場合の例外。
 * 在庫はグループ単位で管理するため、未所属ユーザーは操作不可とする。
 */
public class GroupMembershipRequiredException extends RuntimeException {

    public GroupMembershipRequiredException(String message) {
        super(message);
    }
}

package com.familystock.backend.util;

import java.security.SecureRandom;

/**
 * 招待コード生成ユーティリティ。
 * 推測されにくいコードを生成し、MVPでの不正参加リスクを下げる。
 */
public final class InviteCodeGenerator {

    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int DEFAULT_LENGTH = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private InviteCodeGenerator() {
    }

    /**
     * 招待コードを生成する。
     *
     * @return ランダム英数字コード
     */
    public static String generate() {
        StringBuilder builder = new StringBuilder(DEFAULT_LENGTH);
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(ALPHANUMERIC.length());
            builder.append(ALPHANUMERIC.charAt(index));
        }
        return builder.toString();
    }
}

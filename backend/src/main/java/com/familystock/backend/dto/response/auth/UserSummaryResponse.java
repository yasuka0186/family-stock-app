package com.familystock.backend.dto.response.auth;

import lombok.Builder;
import lombok.Getter;

/**
 * 認証レスポンスで返す最小ユーザー情報。
 * フロント初期表示に必要な項目だけ返す。
 */
@Getter
@Builder
public class UserSummaryResponse {

    private Long id;
    private String name;
    private String email;
}

package com.familystock.backend.dto.response.auth;

import lombok.Builder;
import lombok.Getter;

/**
 * ログインAPIのレスポンスDTO。
 * アクセストークンと最小ユーザー情報を返す。
 */
@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private UserSummaryResponse user;
}

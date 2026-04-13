package com.familystock.backend.dto.response.auth;

import lombok.Builder;
import lombok.Getter;

/**
 * ユーザー登録APIのレスポンスDTO。
 * MVPでは登録成功の識別に必要な情報だけ返す。
 */
@Getter
@Builder
public class RegisterResponse {

    private Long userId;
    private String message;
}

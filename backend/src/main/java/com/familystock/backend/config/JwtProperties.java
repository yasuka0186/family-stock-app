package com.familystock.backend.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT設定値をapplication.ymlから読み込む設定クラス。
 * 認証方式をJWTで固定しつつ、秘密鍵や有効期限を環境ごとに差し替えやすくする。
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    @Positive
    private long accessTokenTtlSeconds;
}

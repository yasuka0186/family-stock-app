package com.familystock.backend.security;

import com.familystock.backend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * JWTの生成・検証を担当するユーティリティ。
 * 署名鍵や有効期限は設定値から読み込み、環境差分に対応する。
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 認証済みユーザー情報からアクセストークンを生成する。
     *
     * @param authentication 認証情報
     * @return 署名済みJWT文字列
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.getAccessTokenTtlSeconds());

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * JWTからユーザー識別子（subject）を取り出す。
     *
     * @param token JWT文字列
     * @return トークン内subject（メールアドレス想定）
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * JWT署名と有効期限を検証する。
     *
     * @param token JWT文字列
     * @return 検証成功時true
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 署名用秘密鍵を生成する。
     *
     * @return HMAC署名用SecretKey
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}

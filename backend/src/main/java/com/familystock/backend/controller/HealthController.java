package com.familystock.backend.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * アプリケーションの生存確認用エンドポイントを提供するコントローラー。
 * 初期フェーズでは起動確認に責務を絞り、DB疎通確認は行わない。
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * ヘルスチェック結果を返す。
     *
     * @return サービス状態を表す固定レスポンス
     */
    @GetMapping
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}

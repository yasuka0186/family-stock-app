package com.familystock.backend.dto.response;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * APIエラーレスポンスの共通フォーマット。
 * フロントエンドが一貫した形式でエラーを扱えるようにする。
 */
@Getter
@Builder
public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
}

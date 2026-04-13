package com.familystock.backend.dto.request.shopping;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 買い物リスト手動追加APIの入力DTO。
 * stockItemIdの有無で入力必須項目が変わるため、詳細検証はサービス層で補完する。
 */
@Getter
@Setter
public class ShoppingListItemCreateRequest {

    private Long stockItemId;

    @Size(max = 120, message = "name length must be 120 or less")
    private String name;

    @Size(max = 20, message = "unit length must be 20 or less")
    private String unit;

    @Size(max = 255, message = "note length must be 255 or less")
    private String note;
}

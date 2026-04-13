package com.familystock.backend.dto.response.shopping;

import lombok.Builder;
import lombok.Getter;

/**
 * 手動追加API専用レスポンスDTO。
 * 重複時に新規作成されなかったことを明示するため created フラグを持つ。
 */
@Getter
@Builder
public class ShoppingListItemCreateResponse {

    private boolean created;
    private ShoppingListItemResponse item;
}

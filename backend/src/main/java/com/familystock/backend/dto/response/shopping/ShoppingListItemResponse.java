package com.familystock.backend.dto.response.shopping;

import com.familystock.backend.domain.entity.shopping.type.ShoppingListSourceType;
import com.familystock.backend.domain.entity.shopping.type.ShoppingListStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 買い物リストAPI共通レスポンスDTO。
 */
@Getter
@Builder
public class ShoppingListItemResponse {

    private Long id;
    private Long stockItemId;
    private String name;
    private String unit;
    private ShoppingListStatus status;
    private ShoppingListSourceType sourceType;
    private String note;
    private LocalDateTime createdAt;
}

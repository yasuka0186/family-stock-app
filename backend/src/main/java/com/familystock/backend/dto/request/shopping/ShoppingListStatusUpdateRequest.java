package com.familystock.backend.dto.request.shopping;

import com.familystock.backend.domain.entity.shopping.type.ShoppingListStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 買い物リスト状態更新APIの入力DTO。
 */
@Getter
@Setter
public class ShoppingListStatusUpdateRequest {

    @NotNull(message = "status is required")
    private ShoppingListStatus status;
}

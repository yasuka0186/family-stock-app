package com.familystock.backend.dto.request;

import com.familystock.backend.dto.request.type.StockUpdateMode;
import com.familystock.backend.dto.request.type.StockUpdateReason;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 在庫更新APIのリクエストDTO。
 * reasonを先行導入することで、将来の在庫履歴機能追加時の破壊的変更を避ける。
 */
@Getter
@Setter
public class StockUpdateRequest {

    @NotNull
    private StockUpdateMode mode;

    @NotNull
    @Min(0)
    private Integer quantity;

    private StockUpdateReason reason;
}

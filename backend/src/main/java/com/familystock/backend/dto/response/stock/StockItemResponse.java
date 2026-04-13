package com.familystock.backend.dto.response.stock;

import lombok.Builder;
import lombok.Getter;

/**
 * 在庫アイテムAPIの返却DTO。
 * Entityを直接返さず、API契約を安定化させる。
 */
@Getter
@Builder
public class StockItemResponse {

    private Long id;
    private Long familyGroupId;
    private String name;
    private String category;
    private String unit;
    private Integer currentStock;
    private Integer minimumStock;
    private boolean lowStock;
    private String createdByEmail;
}

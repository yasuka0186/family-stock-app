package com.familystock.backend.dto.request.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 在庫アイテム作成・更新APIの共通入力DTO。
 */
@Getter
@Setter
public class StockItemUpsertRequest {

    @NotBlank(message = "name is required")
    @Size(max = 120, message = "name length must be 120 or less")
    private String name;

    @Size(max = 50, message = "category length must be 50 or less")
    private String category;

    @NotBlank(message = "unit is required")
    @Size(max = 20, message = "unit length must be 20 or less")
    private String unit;

    @NotNull(message = "currentStock is required")
    @Min(value = 0, message = "currentStock must be 0 or greater")
    private Integer currentStock;

    @NotNull(message = "minimumStock is required")
    @Min(value = 0, message = "minimumStock must be 0 or greater")
    private Integer minimumStock;
}

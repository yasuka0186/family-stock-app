package com.familystock.backend.controller;

import com.familystock.backend.dto.request.stock.StockItemUpsertRequest;
import com.familystock.backend.dto.response.stock.StockItemResponse;
import com.familystock.backend.service.stock.StockItemService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在庫アイテム関連APIを提供するコントローラー。
 * 認証済みかつ所属グループ内のアイテムのみ操作できる。
 */
@RestController
@RequestMapping("/api/stock-items")
@RequiredArgsConstructor
public class StockItemController {

    private final StockItemService stockItemService;

    /**
     * 所属グループの在庫一覧を取得する。
     * MVPでは論理削除済みを除外し、必要に応じて低在庫のみ絞り込む。
     *
     * @param authentication JWT認証済みユーザー
     * @param lowStockOnly 低在庫のみ返すか
     * @return 在庫一覧
     */
    @GetMapping
    public ResponseEntity<List<StockItemResponse>> getStockItems(
            Authentication authentication,
            @RequestParam(defaultValue = "false") boolean lowStockOnly
    ) {
        List<StockItemResponse> response = stockItemService.getStockItems(authentication.getName(), lowStockOnly);
        return ResponseEntity.ok(response);
    }

    /**
     * 所属グループ内の在庫詳細を取得する。
     *
     * @param authentication JWT認証済みユーザー
     * @param id 在庫ID
     * @return 在庫詳細
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockItemResponse> getStockItem(Authentication authentication, @PathVariable Long id) {
        StockItemResponse response = stockItemService.getStockItem(authentication.getName(), id);
        return ResponseEntity.ok(response);
    }

    /**
     * 所属グループへ在庫アイテムを作成する。
     *
     * @param authentication JWT認証済みユーザー
     * @param request 作成入力
     * @return 作成後在庫情報
     */
    @PostMapping
    public ResponseEntity<StockItemResponse> createStockItem(
            Authentication authentication,
            @Valid @RequestBody StockItemUpsertRequest request
    ) {
        StockItemResponse response = stockItemService.createStockItem(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 所属グループ内の在庫アイテムを更新する。
     *
     * @param authentication JWT認証済みユーザー
     * @param id 更新対象ID
     * @param request 更新入力
     * @return 更新後在庫情報
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockItemResponse> updateStockItem(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody StockItemUpsertRequest request
    ) {
        StockItemResponse response = stockItemService.updateStockItem(authentication.getName(), id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 在庫アイテムを論理削除する。
     *
     * @param authentication JWT認証済みユーザー
     * @param id 削除対象ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockItem(Authentication authentication, @PathVariable Long id) {
        stockItemService.deleteStockItem(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}

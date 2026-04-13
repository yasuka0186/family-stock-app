package com.familystock.backend.controller;

import com.familystock.backend.domain.entity.shopping.type.ShoppingListStatus;
import com.familystock.backend.dto.request.shopping.ShoppingListItemCreateRequest;
import com.familystock.backend.dto.request.shopping.ShoppingListStatusUpdateRequest;
import com.familystock.backend.dto.response.shopping.ShoppingListItemCreateResponse;
import com.familystock.backend.dto.response.shopping.ShoppingListItemResponse;
import com.familystock.backend.service.shopping.ShoppingListItemService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 買い物リスト関連APIを提供するコントローラー。
 * 家族グループ単位で、一覧取得・手動追加・状態更新を扱う。
 */
@RestController
@RequestMapping("/api/shopping-list-items")
@RequiredArgsConstructor
public class ShoppingListItemController {

    private final ShoppingListItemService shoppingListItemService;

    /**
     * 買い物リスト一覧を取得する。
     * status未指定時はPENDING中心のMVP運用をデフォルトにする。
     *
     * @param authentication JWT認証済みユーザー
     * @param status optionalの状態フィルタ
     * @return 買い物リスト一覧
     */
    @GetMapping
    public ResponseEntity<List<ShoppingListItemResponse>> getShoppingListItems(
            Authentication authentication,
            @RequestParam(required = false) ShoppingListStatus status
    ) {
        List<ShoppingListItemResponse> response = shoppingListItemService.getShoppingListItems(
                authentication.getName(),
                status
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 買い物リストへ手動追加する。
     * 既に同じstock itemのPENDINGがある場合は重複作成せず既存項目を返す。
     *
     * @param authentication JWT認証済みユーザー
     * @param request 手動追加入力
     * @return 作成結果
     */
    @PostMapping
    public ResponseEntity<ShoppingListItemCreateResponse> createShoppingListItem(
            Authentication authentication,
            @Valid @RequestBody ShoppingListItemCreateRequest request
    ) {
        ShoppingListItemCreateResponse response = shoppingListItemService.createManualItem(
                authentication.getName(),
                request
        );

        if (response.isCreated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 買い物リスト項目の状態を更新する。
     * 物理削除ではなくstatus管理を中心に据えるMVP方針に沿う。
     *
     * @param authentication JWT認証済みユーザー
     * @param id 更新対象ID
     * @param request 状態更新入力
     * @return 更新後項目
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ShoppingListItemResponse> updateStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ShoppingListStatusUpdateRequest request
    ) {
        ShoppingListItemResponse response = shoppingListItemService.updateStatus(
                authentication.getName(),
                id,
                request.getStatus()
        );
        return ResponseEntity.ok(response);
    }
}

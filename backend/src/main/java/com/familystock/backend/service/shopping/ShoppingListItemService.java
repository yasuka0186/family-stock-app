package com.familystock.backend.service.shopping;

import com.familystock.backend.domain.entity.shopping.type.ShoppingListStatus;
import com.familystock.backend.dto.request.shopping.ShoppingListItemCreateRequest;
import com.familystock.backend.dto.response.shopping.ShoppingListItemCreateResponse;
import com.familystock.backend.dto.response.shopping.ShoppingListItemResponse;
import java.util.List;

/**
 * 買い物リストの主要ユースケースを扱うサービス。
 */
public interface ShoppingListItemService {

    /**
     * 所属グループの買い物リストを取得する。
     *
     * @param userEmail JWT subjectとして扱うメール
     * @param status optionalの状態フィルタ
     * @return 買い物リスト一覧
     */
    List<ShoppingListItemResponse> getShoppingListItems(String userEmail, ShoppingListStatus status);

    /**
     * 買い物リストへ手動追加する。
     *
     * @param userEmail JWT subjectとして扱うメール
     * @param request 手動追加入力
     * @return 作成結果（重複時は既存返却）
     */
    ShoppingListItemCreateResponse createManualItem(String userEmail, ShoppingListItemCreateRequest request);

    /**
     * 買い物リスト項目の状態を更新する。
     *
     * @param userEmail JWT subjectとして扱うメール
     * @param itemId 更新対象ID
     * @param status 更新先状態
     * @return 更新後項目
     */
    ShoppingListItemResponse updateStatus(String userEmail, Long itemId, ShoppingListStatus status);
}

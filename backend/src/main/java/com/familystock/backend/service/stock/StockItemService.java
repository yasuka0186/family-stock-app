package com.familystock.backend.service.stock;

import com.familystock.backend.dto.request.stock.StockItemUpsertRequest;
import com.familystock.backend.dto.response.stock.StockItemResponse;
import java.util.List;

/**
 * 在庫アイテムCRUDユースケースを扱うサービス。
 */
public interface StockItemService {

    /**
     * 所属グループの在庫一覧を取得する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param lowStockOnly 低在庫のみ取得するか
     * @return 在庫一覧
     */
    List<StockItemResponse> getStockItems(String userEmail, boolean lowStockOnly);

    /**
     * 所属グループ内の在庫詳細を取得する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param stockItemId 在庫ID
     * @return 在庫詳細
     */
    StockItemResponse getStockItem(String userEmail, Long stockItemId);

    /**
     * 所属グループへ新規在庫アイテムを作成する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param request 作成入力
     * @return 作成後在庫情報
     */
    StockItemResponse createStockItem(String userEmail, StockItemUpsertRequest request);

    /**
     * 所属グループ内の在庫アイテムを更新する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param stockItemId 更新対象ID
     * @param request 更新入力
     * @return 更新後在庫情報
     */
    StockItemResponse updateStockItem(String userEmail, Long stockItemId, StockItemUpsertRequest request);

    /**
     * 所属グループ内の在庫アイテムを論理削除する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param stockItemId 削除対象ID
     */
    void deleteStockItem(String userEmail, Long stockItemId);
}

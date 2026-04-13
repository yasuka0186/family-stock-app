package com.familystock.backend.repository.shopping;

import com.familystock.backend.domain.entity.shopping.ShoppingListItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 買い物リスト項目の永続化アクセスを担当するリポジトリ。
 */
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    /**
     * 所属グループの買い物リストを作成日時降順で取得する。
     *
     * @param familyGroupId 所属グループID
     * @return グループ内買い物リスト
     */
    List<ShoppingListItem> findByFamilyGroupIdOrderByCreatedAtDesc(Long familyGroupId);

    /**
     * 所属グループかつ指定statusの買い物リストを作成日時降順で取得する。
     *
     * @param familyGroupId 所属グループID
     * @param status 状態
     * @return フィルタ済み買い物リスト
     */
    List<ShoppingListItem> findByFamilyGroupIdAndStatusOrderByCreatedAtDesc(Long familyGroupId, String status);

    /**
     * 所属グループ境界を含めて買い物リスト項目を取得する。
     *
     * @param id 買い物リストID
     * @param familyGroupId 所属グループID
     * @return 該当項目
     */
    Optional<ShoppingListItem> findByIdAndFamilyGroupId(Long id, Long familyGroupId);

    /**
     * 同一stockItemに対するPENDING項目の存在確認を行う。
     * MANUAL / AUTO_LOW_STOCK を区別せず、重複を抑止するために利用する。
     *
     * @param familyGroupId 所属グループID
     * @param stockItemId 在庫ID
     * @return PENDING項目が存在すればtrue
     */
    @Query("""
            select count(sli) > 0
            from ShoppingListItem sli
            where sli.familyGroup.id = :familyGroupId
              and sli.stockItem.id = :stockItemId
              and sli.status = 'PENDING'
            """)
    boolean existsPendingByFamilyGroupIdAndStockItemId(
            @Param("familyGroupId") Long familyGroupId,
            @Param("stockItemId") Long stockItemId
    );

    /**
     * 同一stockItemの最新PENDING項目を取得する。
     * 手動追加時に重複なら既存を返す運用に利用する。
     *
     * @param familyGroupId 所属グループID
     * @param stockItemId 在庫ID
     * @return 既存PENDING項目
     */
    Optional<ShoppingListItem> findFirstByFamilyGroupIdAndStockItemIdAndStatusOrderByCreatedAtDesc(
            Long familyGroupId,
            Long stockItemId,
            String status
    );
}

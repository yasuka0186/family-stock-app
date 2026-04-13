package com.familystock.backend.repository.shopping;

import com.familystock.backend.domain.entity.shopping.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 買い物リスト項目の永続化アクセスを担当するリポジトリ。
 */
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

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
}

package com.familystock.backend.repository.stock;

import com.familystock.backend.domain.entity.stock.StockItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 在庫アイテムの永続化アクセスを担当するリポジトリ。
 */
public interface StockItemRepository extends JpaRepository<StockItem, Long> {

    /**
     * 所属グループの有効在庫アイテムを名前順で取得する。
     * 一覧の表示順を安定させ、フロント側の差分描画を扱いやすくする。
     *
     * @param familyGroupId 所属グループID
     * @return 有効在庫一覧
     */
    List<StockItem> findAllByFamilyGroupIdAndIsActiveTrueOrderByNameAsc(Long familyGroupId);

    /**
     * 所属グループかつ有効な在庫アイテムを取得する。
     * 他グループデータの取得を遮断する境界チェックに利用する。
     *
     * @param id 在庫ID
     * @param familyGroupId 所属グループID
     * @return 対象在庫（存在しない場合は空）
     */
    Optional<StockItem> findByIdAndFamilyGroupIdAndIsActiveTrue(Long id, Long familyGroupId);

    /**
     * 同一グループ内で有効な同名在庫が存在するかを判定する。
     *
     * @param familyGroupId 所属グループID
     * @param normalizedName 小文字正規化済みアイテム名
     * @return 同名が存在する場合true
     */
    @Query("""
            select count(si) > 0
            from StockItem si
            where si.familyGroup.id = :familyGroupId
              and lower(si.name) = :normalizedName
              and si.isActive = true
            """)
    boolean existsActiveByFamilyGroupIdAndNormalizedName(
            @Param("familyGroupId") Long familyGroupId,
            @Param("normalizedName") String normalizedName
    );

    /**
     * 更新時に自分自身を除外して同名重複を判定する。
     *
     * @param familyGroupId 所属グループID
     * @param normalizedName 小文字正規化済みアイテム名
     * @param excludeId 重複判定から除外する在庫ID
     * @return 同名が存在する場合true
     */
    @Query("""
            select count(si) > 0
            from StockItem si
            where si.familyGroup.id = :familyGroupId
              and lower(si.name) = :normalizedName
              and si.isActive = true
              and si.id <> :excludeId
            """)
    boolean existsActiveByFamilyGroupIdAndNormalizedNameExcludingId(
            @Param("familyGroupId") Long familyGroupId,
            @Param("normalizedName") String normalizedName,
            @Param("excludeId") Long excludeId
    );
}

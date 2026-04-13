package com.familystock.backend.repository.group;

import com.familystock.backend.domain.entity.group.FamilyMembership;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 家族グループ所属情報の永続化アクセスを担当するリポジトリ。
 */
public interface FamilyMembershipRepository extends JpaRepository<FamilyMembership, Long> {

    /**
     * ユーザーIDから所属情報を取得する。
     * MVPの1ユーザー1グループ前提では0件または1件を想定する。
     *
     * @param userId ユーザーID
     * @return 所属情報（未所属なら空）
     */
    Optional<FamilyMembership> findByUserId(Long userId);

    /**
     * ユーザーが所属済みかを確認する。
     *
     * @param userId ユーザーID
     * @return 所属済みならtrue
     */
    boolean existsByUserId(Long userId);

    /**
     * 同一グループへの二重参加確認に使う。
     *
     * @param familyGroupId グループID
     * @param userId ユーザーID
     * @return 既に同じ組み合わせがある場合true
     */
    boolean existsByFamilyGroupIdAndUserId(Long familyGroupId, Long userId);
}

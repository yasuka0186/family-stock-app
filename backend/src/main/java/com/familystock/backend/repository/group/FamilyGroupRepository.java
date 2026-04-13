package com.familystock.backend.repository.group;

import com.familystock.backend.domain.entity.group.FamilyGroup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 家族グループの永続化アクセスを担当するリポジトリ。
 */
public interface FamilyGroupRepository extends JpaRepository<FamilyGroup, Long> {

    /**
     * 招待コードで家族グループを取得する。
     *
     * @param inviteCode 招待コード
     * @return 該当グループ（存在しない場合は空）
     */
    Optional<FamilyGroup> findByInviteCode(String inviteCode);

    /**
     * 招待コードの既存有無を確認する。
     *
     * @param inviteCode 招待コード
     * @return 既存の場合true
     */
    boolean existsByInviteCode(String inviteCode);
}

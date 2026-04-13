package com.familystock.backend.domain.entity.group;

import com.familystock.backend.domain.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザーと家族グループの所属関係を表すエンティティ。
 * 1ユーザー1グループ制約はサービス層チェックとDB制約の両方で守る。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "family_memberships",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_family_memberships_group_user", columnNames = {"family_group_id", "user_id"})
        }
)
public class FamilyMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_group_id", nullable = false)
    private FamilyGroup familyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}

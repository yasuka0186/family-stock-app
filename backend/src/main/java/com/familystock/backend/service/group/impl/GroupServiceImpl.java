package com.familystock.backend.service.group.impl;

import com.familystock.backend.domain.entity.User;
import com.familystock.backend.domain.entity.group.FamilyGroup;
import com.familystock.backend.domain.entity.group.FamilyMembership;
import com.familystock.backend.dto.request.group.CreateGroupRequest;
import com.familystock.backend.dto.request.group.JoinGroupRequest;
import com.familystock.backend.dto.response.group.GroupSummaryResponse;
import com.familystock.backend.dto.response.group.MyGroupResponse;
import com.familystock.backend.exception.auth.InvalidCredentialsException;
import com.familystock.backend.exception.group.AlreadyInGroupException;
import com.familystock.backend.exception.group.AlreadyMemberException;
import com.familystock.backend.exception.group.InvalidInviteCodeException;
import com.familystock.backend.repository.UserRepository;
import com.familystock.backend.repository.group.FamilyGroupRepository;
import com.familystock.backend.repository.group.FamilyMembershipRepository;
import com.familystock.backend.service.group.GroupService;
import com.familystock.backend.util.InviteCodeGenerator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 家族グループ関連の業務ロジック実装。
 * 1ユーザー1グループ制約をサービス層で明示的に保証する。
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final UserRepository userRepository;
    private final FamilyGroupRepository familyGroupRepository;
    private final FamilyMembershipRepository familyMembershipRepository;

    /**
     * 新しいグループを作成し、作成者を自動所属させる。
     *
     * @param email JWT subjectとして扱うユーザーメール
     * @param request グループ作成入力
     * @return 作成済みグループ情報
     */
    @Override
    @Transactional
    public GroupSummaryResponse createGroup(String email, CreateGroupRequest request) {
        User user = findUserByEmail(email);
        ensureUserIsNotInAnyGroup(user.getId());

        FamilyGroup group = new FamilyGroup();
        group.setName(request.getName().trim());
        group.setInviteCode(generateUniqueInviteCode());
        group.setCreatedBy(user);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());

        FamilyGroup savedGroup = familyGroupRepository.save(group);
        createMembership(savedGroup, user);

        return toGroupSummary(savedGroup);
    }

    /**
     * 招待コードで既存グループへ参加する。
     *
     * @param email JWT subjectとして扱うユーザーメール
     * @param request 参加入力
     * @return 参加先グループ情報
     */
    @Override
    @Transactional
    public GroupSummaryResponse joinGroup(String email, JoinGroupRequest request) {
        User user = findUserByEmail(email);
        // 入力ゆれを吸収するため英大文字に正規化して検索する。
        FamilyGroup targetGroup = familyGroupRepository.findByInviteCode(request.getInviteCode().trim().toUpperCase())
                .orElseThrow(() -> new InvalidInviteCodeException("invalid invite code"));

        familyMembershipRepository.findByUserId(user.getId()).ifPresent(existing -> {
            if (existing.getFamilyGroup().getId().equals(targetGroup.getId())) {
                throw new AlreadyMemberException("already joined this group");
            }
            throw new AlreadyInGroupException("user already belongs to another group");
        });

        if (familyMembershipRepository.existsByFamilyGroupIdAndUserId(targetGroup.getId(), user.getId())) {
            throw new AlreadyMemberException("already joined this group");
        }

        createMembership(targetGroup, user);
        return toGroupSummary(targetGroup);
    }

    /**
     * 認証ユーザーの所属グループを返す。
     * 未所属なら joined=false を返し、フロントの画面分岐を簡単にする。
     *
     * @param email JWT subjectとして扱うユーザーメール
     * @return 所属状態レスポンス
     */
    @Override
    @Transactional(readOnly = true)
    public MyGroupResponse getMyGroup(String email) {
        User user = findUserByEmail(email);

        return familyMembershipRepository.findByUserId(user.getId())
                .map(membership -> MyGroupResponse.builder()
                        .joined(true)
                        .group(toGroupSummary(membership.getFamilyGroup()))
                        .build())
                .orElseGet(() -> MyGroupResponse.builder()
                        .joined(false)
                        .group(null)
                        .build());
    }

    /**
     * ユーザーが未所属か検証する。
     * 1ユーザー1グループ制約の主要ガードとして利用する。
     *
     * @param userId ユーザーID
     */
    private void ensureUserIsNotInAnyGroup(Long userId) {
        if (familyMembershipRepository.existsByUserId(userId)) {
            throw new AlreadyInGroupException("user already belongs to a group");
        }
    }

    /**
     * 招待コードの一意性を担保しながら生成する。
     * TODO: 将来、コード再発行や有効期限が必要になったら生成ポリシーを拡張する。
     *
     * @return 未使用の招待コード
     */
    private String generateUniqueInviteCode() {
        String inviteCode;
        do {
            inviteCode = InviteCodeGenerator.generate();
        } while (familyGroupRepository.existsByInviteCode(inviteCode));
        return inviteCode;
    }

    /**
     * 所属レコードを作成する共通処理。
     *
     * @param group 対象グループ
     * @param user 対象ユーザー
     */
    private void createMembership(FamilyGroup group, User user) {
        FamilyMembership membership = new FamilyMembership();
        membership.setFamilyGroup(group);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());
        familyMembershipRepository.save(membership);
    }

    /**
     * メールアドレスでユーザーを取得する。
     *
     * @param email JWT subjectとして扱うメール
     * @return ユーザー
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("user not found"));
    }

    /**
     * グループエンティティをレスポンスDTOへ変換する。
     *
     * @param group 変換元グループ
     * @return API返却用グループ情報
     */
    private GroupSummaryResponse toGroupSummary(FamilyGroup group) {
        return GroupSummaryResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .inviteCode(group.getInviteCode())
                .build();
    }
}

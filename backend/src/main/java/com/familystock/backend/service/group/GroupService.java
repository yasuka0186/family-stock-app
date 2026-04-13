package com.familystock.backend.service.group;

import com.familystock.backend.dto.request.group.CreateGroupRequest;
import com.familystock.backend.dto.request.group.JoinGroupRequest;
import com.familystock.backend.dto.response.group.GroupSummaryResponse;
import com.familystock.backend.dto.response.group.MyGroupResponse;

/**
 * 家族グループ関連ユースケースを扱うサービス。
 */
public interface GroupService {

    /**
     * 認証済みユーザーの新規グループ作成を行う。
     *
     * @param email JWT subjectとして扱うユーザーメール
     * @param request グループ作成入力
     * @return 作成結果
     */
    GroupSummaryResponse createGroup(String email, CreateGroupRequest request);

    /**
     * 招待コードによるグループ参加を行う。
     *
     * @param email JWT subjectとして扱うユーザーメール
     * @param request 参加入力
     * @return 参加結果
     */
    GroupSummaryResponse joinGroup(String email, JoinGroupRequest request);

    /**
     * 認証済みユーザーの所属グループを返す。
     *
     * @param email JWT subjectとして扱うユーザーメール
     * @return 所属状態とグループ情報
     */
    MyGroupResponse getMyGroup(String email);
}

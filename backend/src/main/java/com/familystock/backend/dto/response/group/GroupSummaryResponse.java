package com.familystock.backend.dto.response.group;

import lombok.Builder;
import lombok.Getter;

/**
 * グループAPI共通の最小レスポンスDTO。
 */
@Getter
@Builder
public class GroupSummaryResponse {

    private Long groupId;
    private String groupName;
    private String inviteCode;
}

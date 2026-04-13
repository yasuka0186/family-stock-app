package com.familystock.backend.dto.response.group;

import lombok.Builder;
import lombok.Getter;

/**
 * 所属グループ取得APIのレスポンスDTO。
 * 未所属状態を明示するため joined フラグを持つ。
 */
@Getter
@Builder
public class MyGroupResponse {

    private boolean joined;
    private GroupSummaryResponse group;
}

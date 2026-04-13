package com.familystock.backend.controller;

import com.familystock.backend.dto.request.group.CreateGroupRequest;
import com.familystock.backend.dto.request.group.JoinGroupRequest;
import com.familystock.backend.dto.response.group.GroupSummaryResponse;
import com.familystock.backend.dto.response.group.MyGroupResponse;
import com.familystock.backend.service.group.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 家族グループ関連APIを提供するコントローラー。
 * グループ作成、招待コード参加、所属グループ取得を扱う。
 */
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 認証済みユーザーの新規グループ作成を行う。
     * 作成者を同時にメンバー登録し、所属整合性を保つ。
     *
     * @param authentication JWT認証済みユーザー情報
     * @param request 作成入力
     * @return 作成済みグループ情報
     */
    @PostMapping
    public ResponseEntity<GroupSummaryResponse> createGroup(
            Authentication authentication,
            @Valid @RequestBody CreateGroupRequest request
    ) {
        GroupSummaryResponse response = groupService.createGroup(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 招待コードで既存グループへ参加する。
     *
     * @param authentication JWT認証済みユーザー情報
     * @param request 参加入力
     * @return 参加先グループ情報
     */
    @PostMapping("/join")
    public ResponseEntity<GroupSummaryResponse> joinGroup(
            Authentication authentication,
            @Valid @RequestBody JoinGroupRequest request
    ) {
        GroupSummaryResponse response = groupService.joinGroup(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 認証済みユーザーの所属グループを取得する。
     * 未所属の場合も200で状態を返し、フロント分岐を簡素化する。
     *
     * @param authentication JWT認証済みユーザー情報
     * @return 所属状態とグループ情報
     */
    @GetMapping("/me")
    public ResponseEntity<MyGroupResponse> myGroup(Authentication authentication) {
        MyGroupResponse response = groupService.getMyGroup(authentication.getName());
        return ResponseEntity.ok(response);
    }
}

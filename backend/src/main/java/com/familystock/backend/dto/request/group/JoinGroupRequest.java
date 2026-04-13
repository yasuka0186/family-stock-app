package com.familystock.backend.dto.request.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * グループ参加APIの入力DTO。
 */
@Getter
@Setter
public class JoinGroupRequest {

    @NotBlank(message = "inviteCode is required")
    @Size(min = 8, max = 20, message = "inviteCode length must be between 8 and 20")
    private String inviteCode;
}

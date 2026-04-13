package com.familystock.backend.dto.request.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * グループ作成APIの入力DTO。
 */
@Getter
@Setter
public class CreateGroupRequest {

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 100, message = "name length must be between 2 and 100")
    private String name;
}

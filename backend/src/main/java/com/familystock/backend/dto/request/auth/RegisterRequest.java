package com.familystock.backend.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * ユーザー登録APIの入力DTO。
 * MVPで必要な最小項目のみ受け取り、余計な属性を持ち込まない。
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 72, message = "password length must be between 8 and 72")
    private String password;
}

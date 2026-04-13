package com.familystock.backend.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * ログインAPIの入力DTO。
 * 認証に必要なemail/passwordのみ受け取る。
 */
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}

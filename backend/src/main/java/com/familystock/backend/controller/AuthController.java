package com.familystock.backend.controller;

import com.familystock.backend.dto.request.auth.LoginRequest;
import com.familystock.backend.dto.request.auth.RegisterRequest;
import com.familystock.backend.dto.response.auth.LoginResponse;
import com.familystock.backend.dto.response.auth.RegisterResponse;
import com.familystock.backend.dto.response.auth.UserSummaryResponse;
import com.familystock.backend.service.AuthService;
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
 * 認証APIを提供するコントローラー。
 * MVPで必要なユーザー登録・ログイン・ログインユーザー取得を公開する。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 新規ユーザーを登録する。
     * MVPでは登録直後の自動ログインは行わず、責務を分離して挙動を明確にする。
     *
     * @param request 登録入力
     * @return 登録結果（userId）
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * ログイン認証を実行し、JWTを返す。
     *
     * @param request ログイン入力
     * @return accessTokenとユーザー情報
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 認証済みユーザー情報を返す。
     * フロント初期表示でのユーザー復元を簡素化するためMVPでも提供する。
     *
     * @param authentication Spring Securityが解決した認証情報
     * @return ログインユーザー情報
     */
    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> me(Authentication authentication) {
        UserSummaryResponse response = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }
}

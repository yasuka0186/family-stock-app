package com.familystock.backend.service;

import com.familystock.backend.dto.request.auth.LoginRequest;
import com.familystock.backend.dto.request.auth.RegisterRequest;
import com.familystock.backend.dto.response.auth.LoginResponse;
import com.familystock.backend.dto.response.auth.RegisterResponse;
import com.familystock.backend.dto.response.auth.UserSummaryResponse;

/**

 * 認証ユースケースを扱うサービス。
 * register/login/me の最小業務ロジックを提供する。
 */
public interface AuthService {

    /**
     * 新規ユーザー登録を実行する。
     *
     * @param request 登録入力情報
     * @return 登録結果
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * ログイン認証を実行しJWTを返す。
     *
     * @param request ログイン入力情報
     * @return JWTとユーザー情報
     */
    LoginResponse login(LoginRequest request);

    /**
     * 認証済みユーザー情報を取得する。
     *
     * @param email JWT subjectとして扱うメールアドレス
     * @return 最小ユーザー情報
     */
    UserSummaryResponse getCurrentUser(String email);
}

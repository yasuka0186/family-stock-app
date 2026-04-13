package com.familystock.backend.service.impl;

import com.familystock.backend.domain.entity.User;
import com.familystock.backend.dto.request.auth.LoginRequest;
import com.familystock.backend.dto.request.auth.RegisterRequest;
import com.familystock.backend.dto.response.auth.LoginResponse;
import com.familystock.backend.dto.response.auth.RegisterResponse;
import com.familystock.backend.dto.response.auth.UserSummaryResponse;
import com.familystock.backend.exception.auth.DuplicateEmailException;
import com.familystock.backend.exception.auth.InvalidCredentialsException;
import com.familystock.backend.repository.UserRepository;
import com.familystock.backend.security.JwtTokenProvider;
import com.familystock.backend.service.AuthService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認証関連の業務ロジック実装。
 * MVPでは必要最小限の登録・ログイン・ユーザー取得に責務を限定する。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 新規ユーザーを登録する。
     * メール重複を防ぎ、パスワードは必ずハッシュ化して保存する。
     *
     * @param request 登録入力情報
     * @return 登録成功情報
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new DuplicateEmailException("email already exists");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        return RegisterResponse.builder()
                .userId(saved.getId())
                .message("registered")
                .build();
    }

    /**
     * ログイン認証を行い、アクセストークンを発行する。
     * セキュリティ上、失敗時は詳細を伏せた共通メッセージを返す。
     *
     * @param request ログイン入力情報
     * @return JWTとユーザー情報
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("invalid email or password");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(toUserSummary(user))
                .build();
    }

    /**
     * JWT subject（email）から認証済みユーザーを取得する。
     *
     * @param email JWT subjectとして扱うメールアドレス
     * @return 最小ユーザー情報
     */
    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("user not found"));
        return toUserSummary(user);
    }

    /**
     * ドメインユーザーをレスポンスDTOへ変換する。
     *
     * @param user 変換元ユーザー
     * @return API返却用ユーザー情報
     */
    private UserSummaryResponse toUserSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

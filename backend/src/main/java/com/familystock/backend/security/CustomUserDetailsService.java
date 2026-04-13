package com.familystock.backend.security;

import com.familystock.backend.domain.entity.User;
import com.familystock.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security向けのユーザー取得サービス。
 * MVPではメールアドレスをログインIDとして扱う前提に合わせる。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * ユーザー名（=メールアドレス）から認証用ユーザー情報を生成する。
     *
     * @param username メールアドレス
     * @return Spring SecurityのUserDetails
     * @throws UsernameNotFoundException 該当ユーザーが存在しない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_USER")
                .build();
    }
}

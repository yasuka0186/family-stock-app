package com.familystock.backend.repository;

import com.familystock.backend.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ユーザー永続化アクセスを担うリポジトリ。
 * 認証処理でメールアドレス検索を利用する。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスでユーザーを取得する。
     *
     * @param email 検索対象メールアドレス
     * @return 該当ユーザー（存在しない場合は空）
     */
    Optional<User> findByEmail(String email);
}

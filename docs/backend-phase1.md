# Backend実装フェーズ1（Spring Boot土台構築）

## 1. 目的
- 認証・グループ・在庫・買い物リスト実装を乗せる前段として、最小のSpring Boot実行基盤を整える。
- MVP方針に従い、過剰実装を避けて「次フェーズを進めやすい構成」を優先する。

## 2. 今回作成したbackend構成
- `controller`: HTTPエンドポイント
- `service`: ビジネスロジック層（現時点はAuthServiceの雛形のみ）
- `repository`: JPA Repository
- `domain/entity`: JPA Entity
- `dto/request`, `dto/response`: API入出力用DTO
- `config`: セキュリティ・設定クラス
- `security`: JWT関連クラス・認証フィルタ
- `exception`: 共通例外ハンドラ

## 3. 採用した依存関係
- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- PostgreSQL Driver
- Flyway
- JJWT（`jjwt-api`, `jjwt-impl`, `jjwt-jackson`）
- Lombok

### Lombokを使う理由
- 雛形段階でのボイラープレート（getter/setter/constructor/builder）を減らし、
  実装の見通しを保つため。

## 4. application.yml方針
- ローカル開発向けのサンプル値を設定。
- DB接続・JWT secret・TTLは環境変数で上書き可能。
- 本番値は入れない（`JWT_SECRET`等を環境変数で注入する前提）。

## 5. Flyway初期スキーマ
- `V1__init_schema.sql` で以下を作成。
  - `users`
  - `family_groups`
  - `family_memberships`
  - `stock_items`
  - `shopping_list_items`
- 主な制約
  - `users.email` unique
  - `family_memberships(family_group_id, user_id)` unique
  - `stock_items(family_group_id, name)` unique
  - `current_stock >= 0`, `minimum_stock >= 0`
  - `shopping_list_items.status` は `PENDING/BOUGHT/SKIPPED`
  - `source_type` は `AUTO_LOW_STOCK/MANUAL`
  - `status='PENDING' AND stock_item_id IS NOT NULL` 条件で部分ユニークインデックス

## 6. 認証土台（最小）
- `SecurityConfig`
  - stateless
  - `/api/health` と `/api/auth/**` は認証不要
- `PasswordEncoder`
  - BCrypt
- `JwtTokenProvider`
  - token生成 / 検証 / subject抽出
- `JwtAuthenticationFilter`
  - Bearer TokenがあればSecurityContextに反映
- `CustomUserDetailsService`
  - `users.email` でユーザー解決

## 7. 共通例外処理
- `GlobalExceptionHandler` で以下を統一。
  - バリデーションエラー（400）
  - 想定外エラー（500）
- 返却形式は `timestamp`, `status`, `error`, `message`, `path`, `validationErrors`。

## 8. ヘルスチェック
- `GET /api/health` で `{"status":"UP"}` を返却。
- DB接続確認は未実装（起動/HTTP応答確認のみに限定）。

## 9. ローカル起動手順
```bash
cd backend
./gradlew bootRun
```

Gradle Wrapper未生成環境では以下。
```bash
cd backend
gradle bootRun
```

ヘルス確認例。
```bash
curl http://localhost:8080/api/health
```

## 10. 今回未実装（次フェーズ以降）
- register/login完成実装
- グループ作成/参加API
- 在庫CRUD
- 買い物リストAPI
- 在庫更新時の自動買い物リスト追加本実装
- 在庫履歴テーブル
- 本番向け設定と運用

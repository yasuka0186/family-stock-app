# 食材・生活用品管理家族共有アプリ（仮） MVP設計

## 前提・仮定
- 対象は同居家族の小規模グループ（2〜6人程度）を想定する。
- 1ユーザーはMVP時点では1つの家族グループにのみ所属できる。
- 認証はメールアドレス + パスワード（JWTベース）で開始する。
- 在庫数の単位は文字列（例: `個`, `本`, `袋`）で管理し、MVPでは単位換算はしない。
- 自動買い物リスト追加は「在庫更新API」実行時に同期処理で判定する（バッチや非同期キューは使わない）。

---

## 1. MVPの目的
- 家族全員が同じ在庫情報・買い物対象を同時に確認できる状態を最短で作る。
- 買い忘れ・重複購入を減らすため、最低在庫しきい値による自動買い物リスト追加を実現する。
- 個人開発で継続しやすいように、責務分離された最小構成（Backend / Frontend / DB）で立ち上げる。

## 2. MVP機能一覧

### 2-1. 必須機能（MVP In Scope）
1. ユーザー登録・ログイン
2. 家族グループ作成 / 招待コードで参加
3. 在庫アイテム登録（名前・カテゴリ・現在在庫・最低在庫・単位）
4. 在庫一覧表示（家族単位）
5. 在庫数更新（増減・直接入力）
6. 在庫不足判定（`current_stock <= minimum_stock`）
7. 買い物リスト手動追加
8. 買い物リスト共有表示（家族単位）
9. 在庫更新時の自動買い物リスト追加
   - 重複防止
   - 手動追加との整合性
   - 在庫回復時の扱いを定義

<<<<<<< ours
=======
1.  在庫更新理由（`reason`）の受け取り
   - `CONSUME` / `PURCHASE` / `ADJUST`（任意入力）
   - MVPでは履歴テーブル未導入だが、将来の在庫履歴機能に拡張しやすい入力を先行定義
>>>>>>> theirs
=======
1.  在庫更新理由（`reason`）の受け取り
   - `CONSUME` / `PURCHASE` / `ADJUST`（任意入力）
   - MVPでは履歴テーブル未導入だが、将来の在庫履歴機能に拡張しやすい入力を先行定義
>>>>>>> theirs
=======
1.  在庫更新理由（`reason`）の受け取り
   - `CONSUME` / `PURCHASE` / `ADJUST`（任意入力）
   - MVPでは履歴テーブル未導入だが、将来の在庫履歴機能に拡張しやすい入力を先行定義
>>>>>>> theirs
=======
1.  在庫更新理由（`reason`）の受け取り
   - `CONSUME` / `PURCHASE` / `ADJUST`（任意入力）
   - MVPでは履歴テーブル未導入だが、将来の在庫履歴機能に拡張しやすい入力を先行定義
>>>>>>> theirs

### 2-2. あればよい機能（MVP+1候補）
- 在庫一覧の簡易フィルタ（カテゴリ / 在庫不足のみ）
- 買い物リストの購入済みチェック
- アイテムのソフト削除
- 招待コードの期限設定

## 3. 今回は除外する機能
- バーコード読み取り
- レシートOCR
- 通知（Push / メール / LINE連携）
- 高度な賞味期限管理
- カレンダー連携
- 外部価格比較
- 分析・ダッシュボード
- 細かい権限分離（管理者/一般など）

## 4. 画面一覧

1. ログイン画面
   - 役割: 既存ユーザー認証
   - 主要操作: メール・パスワード入力、ログイン
2. ユーザー登録画面
   - 役割: 新規ユーザー作成
   - 主要操作: 名前、メール、パスワード入力
3. グループ選択/作成画面
   - 役割: グループ作成 or 招待コード参加
   - 主要操作: グループ名入力、招待コード入力
4. 在庫一覧画面（ホーム）
   - 役割: 現在在庫の確認
   - 主要操作: 一覧表示、不足アイテム強調、在庫更新ボタン、アイテム追加遷移
5. 在庫アイテム作成/編集画面
   - 役割: アイテム登録・更新
   - 主要操作: 名前/カテゴリ/単位/現在在庫/最低在庫の入力
6. 買い物リスト画面
   - 役割: 購入対象の共有
   - 主要操作: 手動追加、購入済み化、削除（任意）

## 5. 画面遷移案
- 未ログイン
  - `ログイン` ↔ `ユーザー登録`
- 初回ログイン後（グループ未所属）
  - `グループ選択/作成`
- グループ所属後
  - `在庫一覧（ホーム）` → `在庫アイテム作成/編集`
  - `在庫一覧（ホーム）` ↔ `買い物リスト`
  - グローバルナビ: 在庫 / 買い物リスト / ログアウト

## 6. エンティティ / テーブル設計案

### 6-1. エンティティ一覧
- User
- FamilyGroup
- FamilyMembership
- StockItem
- ShoppingListItem

### 6-2. テーブル案

#### users
- `id` (bigserial, PK)
- `name` (varchar 100, not null)
- `email` (varchar 255, not null, unique)
- `password_hash` (varchar 255, not null)
- `created_at`, `updated_at` (timestamp)

#### family_groups
- `id` (bigserial, PK)
- `name` (varchar 100, not null)
- `invite_code` (varchar 20, not null, unique)
- `created_by` (FK -> users.id)
- `created_at`, `updated_at` (timestamp)

#### family_memberships
- `id` (bigserial, PK)
- `family_group_id` (FK -> family_groups.id, not null)
- `user_id` (FK -> users.id, not null)
- `joined_at` (timestamp)
- 制約: `unique(family_group_id, user_id)`

#### stock_items
- `id` (bigserial, PK)
- `family_group_id` (FK -> family_groups.id, not null)
- `name` (varchar 120, not null)
- `category` (varchar 50, null)
- `unit` (varchar 20, not null, default '個')
- `current_stock` (integer, not null, default 0)
- `minimum_stock` (integer, not null, default 0)
- `is_active` (boolean, not null, default true)
- `created_by` (FK -> users.id)
- `created_at`, `updated_at` (timestamp)
- 制約: `check(current_stock >= 0)`, `check(minimum_stock >= 0)`

=======
- 重複防止（MVP）: `unique(family_group_id, name)`
  - 同一家族グループ内で同名アイテム重複を防ぐ
  - カテゴリ違い・表記ゆれ対応はMVP対象外（将来の正規化で対応）
=======


#### shopping_list_items
- `id` (bigserial, PK)
- `family_group_id` (FK -> family_groups.id, not null)
- `stock_item_id` (FK -> stock_items.id, null)
- `name_snapshot` (varchar 120, not null)
- `unit_snapshot` (varchar 20, not null)
- `status` (varchar 20, not null, default 'PENDING')
- `source_type` (varchar 20, not null)  
  - `AUTO_LOW_STOCK` / `MANUAL`
- `note` (varchar 255, null)
- `created_by` (FK -> users.id)
- `created_at`, `updated_at` (timestamp)
<<<<<<< ours

- 重複防止（MVP）:
  - `unique(family_group_id, stock_item_id, status)` を `status='PENDING'` 条件の部分ユニークインデックスで付与
  - `stock_item_id is null` の手動追加は完全重複判定が難しいため、MVPでは同名重複を許容（将来改善）

- （MVP未採用）`deleted_at` (timestamp, null) または `is_active` による論理削除は将来拡張候補
- 重複防止（MVP）:
  - `unique(family_group_id, stock_item_id, status)` を `status='PENDING'` 条件の部分ユニークインデックスで付与
  - `stock_item_id is null` の手動追加は完全重複判定が難しいため、MVPでは同名重複を許容（将来改善）
- 状態管理（MVP）:
  - 物理削除は必須にしない
  - `status` で管理（`PENDING` / `BOUGHT` / `SKIPPED`）
  - `BOUGHT` / `SKIPPED` は履歴として残す
  - 一覧APIのデフォルトは `PENDING` 中心で返却（必要に応じてstatus指定で履歴参照）


### 6-3. リレーション
- `users` 1 - n `family_memberships` n - 1 `family_groups`
- `family_groups` 1 - n `stock_items`
- `family_groups` 1 - n `shopping_list_items`
- `stock_items` 1 - n `shopping_list_items`（手動自由入力時はnull可）

### 6-4. 自動追加ロジック設計（重要）



=======
1. 在庫更新APIで`stock_items.current_stock`更新（`reason`も受け取る）

2. 更新後に`current_stock <= minimum_stock`を判定
3. trueの場合、`shopping_list_items`にPENDING行があるか確認
   - 対象: `family_group_id + stock_item_id + status=PENDING`
4. なければ`source_type=AUTO_LOW_STOCK`で1件追加
5. あれば何もしない（重複防止）


#### 在庫更新reasonの扱い（MVP）
- 受け付ける値: `CONSUME` / `PURCHASE` / `ADJUST`（optional）
- MVPでは履歴テーブルは作成しない（過剰設計を避ける）
- ただし、在庫更新APIで`reason`を先に受けることで、将来`stock_histories`等を追加する際のAPI破壊的変更を減らす

#### 手動追加との整合性
- 手動で同一`stock_item_id`追加時も、PENDINGがあれば新規作成しない。
- 手動追加で`stock_item_id`未指定（自由入力）の場合は別管理として扱う。

#### 在庫回復時の扱い（MVP方針）
- `current_stock > minimum_stock`に回復しても、自動では削除しない。
- 理由: 「買う予定だったが既に必要ない」をユーザーが明示的に判断しやすくするため。
- 運用: ユーザーが`購入済み`または`不要`でクローズする。

## 7. API一覧

### 認証
1. `POST /api/auth/register`
   - 目的: ユーザー登録
   - 入力: `name, email, password`
   - 出力: `userId`
2. `POST /api/auth/login`
   - 目的: ログイン
   - 入力: `email, password`
   - 出力: `accessToken, user`

### グループ
3. `POST /api/groups`
   - 目的: グループ作成
   - 入力: `name`
   - 出力: `groupId, inviteCode`
4. `POST /api/groups/join`
   - 目的: 招待コード参加
   - 入力: `inviteCode`
   - 出力: `groupId`
5. `GET /api/groups/me`
   - 目的: 自分の所属グループ取得
   - 出力: `group`

### 在庫
6. `GET /api/stock-items`
   - 目的: 在庫一覧
   - クエリ: `lowStockOnly`（任意）
7. `POST /api/stock-items`
   - 目的: 在庫アイテム登録
   - 入力: `name, category, unit, currentStock, minimumStock`
8. `PUT /api/stock-items/{id}`
   - 目的: 在庫アイテム編集
9. `PATCH /api/stock-items/{id}/stock`
   - 目的: 在庫数更新（増減・直接設定）

   - 入力: `mode(SET|ADD|SUBTRACT), quantity`
   - 挙動: 更新後に低在庫判定し、必要なら買い物リスト自動追加

### 買い物リスト
10. `GET /api/shopping-list-items`
    - 目的: 買い物リスト一覧（PENDING中心）


   - 入力: `mode(SET|ADD|SUBTRACT), quantity, reason(optional)`
   - `reason`: `CONSUME` / `PURCHASE` / `ADJUST`
   - 挙動: 更新後に低在庫判定し、必要なら買い物リスト自動追加
   - リクエストDTO案:
     - `mode: StockUpdateMode`
     - `quantity: Integer`
     - `reason: StockUpdateReason?`

### 買い物リスト
10. `GET /api/shopping-list-items`
    - 目的: 買い物リスト一覧（デフォルトはPENDING中心）
    - クエリ: `status`（任意、未指定時は`PENDING`）

=======

1.  `POST /api/shopping-list-items`
    - 目的: 手動追加
    - 入力: `stockItemId(任意), name, unit, note`
    - 挙動: `stockItemId`指定時は重複チェック
2.  `PATCH /api/shopping-list-items/{id}/status`
    - 目的: 状態更新
    - 入力: `status(PENDING|BOUGHT|SKIPPED)`

=======
    - 補足: `BOUGHT` / `SKIPPED`は履歴として保持し、物理削除はMVP必須としない


## 8. ディレクトリ構成案

```text
/backend
  /src/main/java/com/example/familystock
    /controller
    /service
    /repository
    /domain
      /entity
    /dto
      /request
      /response
    /config
    /security
  /src/main/resources
    application.yml
    db/migration

/frontend
  /src
    /views
    /components
    /stores
    /api
    /router
    /types

/docs
  mvp-design.md
  api-spec.md (次ステップで作成)
  er-diagram.md (次ステップで作成)
```

### 最初に作るべき土台
- Backend
  - Spring Initializr雛形
  - 共通例外ハンドラ
  - 認証基盤（JWTフィルタ最小）
  - Flyway初期マイグレーション
- Frontend
  - Vue + TS + Router + Pinia
  - ログイン状態ストア
  - APIクライアント（axios）
  - 最低限レイアウト（ヘッダー + ナビ）

## 9. 実装ステップ
1. 設計確定（本ドキュメントレビュー）
2. DB初期スキーマ作成（users, family_groups, memberships, stock_items, shopping_list_items）
3. 認証API実装（register/login）
4. グループ作成・参加API実装
5. 在庫CRUD + 在庫更新API実装
6. 在庫更新時の自動買い物リスト追加ロジック実装
7. 買い物リストAPI実装（手動追加・一覧・状態更新）
8. フロント画面実装（ログイン→グループ→在庫→買い物リスト）
9. E2E手動確認（最低在庫判定、重複防止、手動追加整合性）

### 「最初に動く最小機能」到達条件
- 1ユーザーがログインできる
- グループを作成できる
- 在庫アイテムを1件登録し、在庫を減らせる
- しきい値以下で買い物リストに自動追加される

## 10. 技術的な懸念点・先に決めるべきこと
1. 認証方式
   - MVPはJWT固定で進めるか、セッション方式にするか
2. グループ所属制約
   - 1ユーザー1グループ固定をいつまで維持するか
3. 招待コード仕様
   - 桁数、再生成、期限の要否
4. 在庫更新競合
   - 同時更新時の整合性（楽観ロック導入時期）
5. 自動追加解除ポリシー
   - 在庫回復時に自動クローズするか（MVPではしない）
6. 手動自由入力アイテムの重複判定
   - 同名・同単位の正規化ルールを導入する時期

7. stock_items同名制約の運用
   - `unique(family_group_id, name)`採用時の表記ゆれ（例: `トマト` / `とまと`）対応方針
   - MVPではアプリ側バリデーション + DB一意制約で簡易対応し、正規化・別名管理は将来対応
8. 買い物リスト終了データの保持期間
   - `BOUGHT` / `SKIPPED` をどこまで保持するか（MVPは無期限保持）
   - 削除要求が増えた場合に `deleted_at` / `is_active` の導入を検討


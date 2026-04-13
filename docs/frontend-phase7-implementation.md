# Frontend実装フェーズ7（画面実装 + API接続）

## 1. フロントエンド構成
- `src/router`: 画面遷移とroute guard
- `src/stores`: 認証状態・グループ所属状態（Pinia）
- `src/api`: axiosクライアントと機能別API呼び出し
- `src/views`: 各主要画面
- `src/components`: 在庫数更新UIなど最小共通部品
- `src/types`: APIレスポンス/リクエスト型
- `src/layouts`: 認証後画面の共通レイアウト

## 2. 画面一覧とルーティング
- `/login`: ログイン画面
- `/register`: ユーザー登録画面
- `/group-setup`: グループ作成/参加画面
- `/stocks`: 在庫一覧画面
- `/stocks/new`: 在庫作成画面
- `/stocks/:id/edit`: 在庫編集画面
- `/shopping-list`: 買い物リスト画面

## 3. 状態管理方針
- `auth` ストア:
  - token, user を管理
  - localStorageへ保存
- `group` ストア:
  - 所属グループを管理
  - `fetchMyGroup` で所属状態を取得

### token保存にlocalStorageを使う理由
- MVPで導入が簡単
- ページリロード後もログイン状態を維持しやすい

## 4. 画面遷移ガード
- 未ログイン:
  - `/login`, `/register` のみ
- ログイン済み + グループ未所属:
  - `/group-setup` へ誘導
- ログイン済み + グループ所属済み:
  - `/stocks`, `/shopping-list` へアクセス可

## 5. API接続方針
- 共通axiosクライアントで以下を統一
  - Authorizationヘッダー付与
  - 401時の最小共通ハンドリング（ログアウト相当）
- API呼び出しは `src/api/*` に集約し、viewへの直書きを削減

## 6. 主要画面の操作概要
### ログイン / 登録
- register/login API接続
- ログイン後は所属グループ有無で遷移先を分岐

### グループ作成 / 参加
- 作成API / 招待コード参加API接続
- 所属済みなら在庫一覧へ遷移

### 在庫一覧
- 一覧表示
- `lowStockOnly` フィルタ
- 在庫不足を視覚表示
- 作成/編集/削除導線
- 在庫数更新（SET/ADD/SUBTRACT/reason）

### 在庫作成 / 編集
- 共通フォームで作成/編集両対応
- 入力チェック（必須/0以上）

### 買い物リスト
- 一覧表示（statusフィルタ）
- 手動追加（stockItemIdあり/なし）
- 状態更新（BOUGHT/SKIPPED/PENDING）

## 7. MVP時点で未実装のUI機能
- 高度なデザイン調整
- 通知UI
- オフライン対応
- 一括更新UI
- 詳細検索/ページング

## 8. 次フェーズ候補
- エラーメッセージのフィールド単位表示改善
- 在庫/買い物リスト画面のコンポーネント分割強化
- 簡易テスト（Vitest）導入
- E2Eテスト導入
<<<<<<< ours
=======

## 9. 最低限の操作手順（ローカル確認用）
1. backendを起動する（`http://localhost:8080`）。
2. frontendで依存をインストールし、開発サーバーを起動する。
3. `/register` でユーザー登録後、`/login` でログインする。
4. グループ未所属なら `/group-setup` で作成または招待コード参加する。
5. `/stocks` で在庫を新規作成し、編集・数量更新を試す。
6. `/shopping-list` で手動追加・状態更新（BOUGHT/SKIPPED）を試す。
7. ログアウト後に保護画面へ直接アクセスし、`/login` へ戻ることを確認する。
>>>>>>> theirs

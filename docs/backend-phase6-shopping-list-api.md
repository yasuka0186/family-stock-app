# Backend実装フェーズ6（買い物リストAPI実装）

## 1. 目的
- 家族グループ単位で買い物リストを参照・手動追加・状態更新できるようにする。
- フェーズ5の低在庫自動追加と矛盾しない整合性ルールを明確化する。

## 2. 実装API

### GET /api/shopping-list-items
- 認証: 必須
- デフォルト: `status` 未指定時は `PENDING` のみ返す
- 任意クエリ: `status=PENDING|BOUGHT|SKIPPED`
- 並び順: `createdAt desc`
- 採用理由:
  - 直近追加を先頭表示すると日常運用で確認しやすい

### POST /api/shopping-list-items
- 認証: 必須
- 入力:
  - `stockItemId` (任意)
  - `name` (stockItemIdなし時は必須)
  - `unit` (stockItemIdなし時は必須)
  - `note` (任意)
- 振る舞い:
  - `stockItemId` あり:
    - 所属グループ内の有効stock itemのみ許可
    - 同一stockItemIdで既存PENDINGがあれば新規作成せず既存を返す
    - `sourceType=MANUAL`, `status=PENDING`
    - `nameSnapshot/unitSnapshot` はstock itemから採用
  - `stockItemId` なし:
    - 自由入力として追加
    - MVPでは同名重複は許容
    - `sourceType=MANUAL`, `status=PENDING`

#### 重複時レスポンス方針
- `created=false` と既存アイテムを返す（no-opを明確化）
- 理由: フロント側が「追加済み」を即判定しやすい

### PATCH /api/shopping-list-items/{id}/status
- 認証: 必須
- 入力: `status` (`PENDING | BOUGHT | SKIPPED`)
- 更新ルール:
  - `PENDING -> BOUGHT/SKIPPED` 可能
  - `BOUGHT/SKIPPED -> PENDING` 再オープンも許可
- 再オープン許可理由:
  - MVPでは誤操作修正を簡単にする方が運用しやすいため

## 3. status と sourceType の意味
- `status`
  - `PENDING`: これから買う
  - `BOUGHT`: 購入済み
  - `SKIPPED`: 今回は見送り
- `sourceType`
  - `MANUAL`: ユーザー手動追加
  - `AUTO_LOW_STOCK`: 在庫更新時の自動追加

## 4. 手動追加と自動追加の整合性ルール
- 同一 `stockItemId` で `PENDING` が既に存在する場合は新規作成しない
  - 既存が `MANUAL` でも `AUTO_LOW_STOCK` でも重複禁止
- `stockItemId = null` の自由入力は別物として扱う
- 在庫回復時に自動クローズしない方針は維持する

## 5. 物理削除APIを作らない理由
- MVPでは状態管理（status）中心で十分に運用できる
- 物理削除を急がず、履歴活用や誤操作リカバリの余地を残す

## 6. エラーレスポンス方針
- 400: バリデーションエラー / 手動追加入力不正
- 401: 未認証
- 403: グループ未所属
- 404: 買い物リスト未存在 / 他グループアクセス / 不正stockItemId
- 500: 想定外エラー

## 7. curl例
```bash
# list (default = pending)
curl http://localhost:8080/api/shopping-list-items \
  -H 'Authorization: Bearer <token>'

# list with status
curl 'http://localhost:8080/api/shopping-list-items?status=BOUGHT' \
  -H 'Authorization: Bearer <token>'

# manual add with stock item
curl -X POST http://localhost:8080/api/shopping-list-items \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"stockItemId":1,"note":"週末用"}'

# manual add free input
curl -X POST http://localhost:8080/api/shopping-list-items \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"name":"キッチンペーパー","unit":"個","note":"セール時"}'

# status update
curl -X PATCH http://localhost:8080/api/shopping-list-items/10/status \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"status":"BOUGHT"}'
```

## 8. 今回未実装
- 買い物リスト削除API
- 一括更新API
- 高度検索/ページング
- 自由入力重複正規化
- 買い物履歴専用画面向けAPI

## 9. 次フェーズ候補
- 買い物リスト削除API（要件次第で論理削除）
- 在庫履歴機能（reason永続化）
- 買い物リスト通知機能
- 買い物リストAPIの統合テスト追加

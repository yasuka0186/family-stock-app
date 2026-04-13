# Backend実装フェーズ5（在庫数更新専用API + 低在庫自動追加）

## 1. 目的
- 在庫数更新を専用APIへ分離し、数量変動ロジックを安全に扱う。
- 更新直後に低在庫判定し、必要なら買い物リストへ自動追加する。

## 2. 実装API

### PATCH /api/stock-items/{id}/stock
- 認証: 必須
- 入力:
  - `mode`: `SET | ADD | SUBTRACT`（必須）
  - `quantity`: 0以上（必須）
  - `reason`: 任意（`CONSUME | PURCHASE | ADJUST`）
- 挙動:
  - `SET`: 在庫数を置換
  - `ADD`: 加算
  - `SUBTRACT`: 減算（負数化する場合は400）
- 出力:
  - 更新後在庫情報（`lowStock` 含む）

## 3. reason の扱い
- reason は将来の在庫履歴機能に備えて入力として受ける。
- MVPでは履歴テーブル保存はまだ行わない。
- 理由:
  - 先にAPI契約を確定し、後続で履歴保存を追加しやすくするため。

## 4. 低在庫判定ルール
- 判定式: `currentStock <= minimumStock`
- 判定タイミング: 在庫更新直後（同期処理）
- Service層で判定し、Controllerには業務ロジックを置かない。

## 5. 自動追加ルール
- 低在庫時に `shopping_list_items` へ自動追加。
- 設定値:
  - `status = PENDING`
  - `sourceType = AUTO_LOW_STOCK`
  - `stockItemId` を紐づけ
  - `nameSnapshot / unitSnapshot` は更新後アイテムから採用
- `createdBy` は操作ユーザーを採用。
  - 理由: だれの操作で自動追加されたかを最低限追跡しやすい。

## 6. 重複防止ルール
- 基本キー: `family_group_id + stock_item_id + status=PENDING`
- 実装方針:
  - DBの部分ユニークインデックスを前提
  - 追加前にアプリ側でも `exists` で確認
- 既にPENDINGがある場合は何もしない（エラーにしない）

## 7. 手動追加との整合性
- 同一 `stockItemId` のPENDINGが既にある場合、
  MANUAL由来/AUTO由来に関わらず自動追加しない。
- `stockItemId = null` の自由入力手動項目は別物扱い（今回対象外）。

## 8. 在庫回復時の扱い
- `currentStock > minimumStock` に回復しても自動クローズしない。
- 理由:
  - 購買判断はユーザー主導で行う方がMVP運用で安全。

## 9. エラーレスポンス方針
- 400: バリデーションエラー / 不正更新（例: 負数化）
- 401: 未認証
- 403: グループ未所属
- 404: 在庫未存在 / 他グループアクセス
- 409: 同名重複（既存CRUD）
- 500: 想定外エラー

## 10. curl例
```bash
# SET
curl -X PATCH http://localhost:8080/api/stock-items/1/stock \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"mode":"SET","quantity":3,"reason":"ADJUST"}'

# ADD
curl -X PATCH http://localhost:8080/api/stock-items/1/stock \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"mode":"ADD","quantity":2,"reason":"PURCHASE"}'

# SUBTRACT
curl -X PATCH http://localhost:8080/api/stock-items/1/stock \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"mode":"SUBTRACT","quantity":1,"reason":"CONSUME"}'
```

## 11. 今回未実装
- 買い物リスト一覧/手動追加/状態更新API
- 在庫履歴テーブルへのreason保存
- 自動クローズ機能
- 非同期処理（キュー/バッチ）

## 12. 次フェーズ候補
- 買い物リストAPI本体実装
- 在庫履歴機能（reason保存）
- 通知機能の検討
- 在庫更新/自動追加の統合テスト追加

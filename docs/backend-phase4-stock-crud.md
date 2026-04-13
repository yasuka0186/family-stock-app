# Backend実装フェーズ4（在庫アイテムCRUD実装）

## 1. 目的
- 認証済みかつ所属グループユーザーが在庫アイテムCRUDを行えるようにする。
- MVP方針として、在庫更新専用APIや買い物リスト自動追加はまだ実装しない。

## 2. 実装API

### GET /api/stock-items
- 認証: 必須
- 挙動:
  - 自分の所属グループ + `isActive=true` の在庫のみ返す
  - `lowStockOnly=true` で `currentStock <= minimumStock` のみ返す
- 並び順: `name asc`
- 採用理由:
  - 名前順は日常利用で見つけやすく、更新頻度に依存せず一覧が安定する

### GET /api/stock-items/{id}
- 認証: 必須
- 挙動:
  - 所属グループ + `isActive=true` の在庫のみ取得
  - 他グループアクセスや未存在は同じ404で返す

### POST /api/stock-items
- 認証: 必須
- 入力:
  - `name` (必須)
  - `category` (任意)
  - `unit` (必須)
  - `currentStock` (0以上)
  - `minimumStock` (0以上)
- 挙動:
  - 所属グループで作成
  - 同グループ同名（大文字小文字無視）重複は409

### PUT /api/stock-items/{id}
- 認証: 必須
- 更新項目:
  - `name`, `category`, `unit`, `currentStock`, `minimumStock`
- 挙動:
  - 所属グループ内のみ更新可
  - 更新時も同名重複チェック（自分以外）

### DELETE /api/stock-items/{id}
- 認証: 必須
- 振る舞い:
  - 物理削除せず `isActive=false` にする論理削除
- 方針理由:
  - MVPでも削除復元や監査拡張に備え、破壊的削除を避ける

## 3. アクセス制御方針
- すべての在庫処理で「ユーザー取得 → 所属グループ取得」をサービス層で実施。
- グループ未所属は403を返す。
- 在庫取得は `id + familyGroupId + isActive=true` 条件で検索し、
  越境アクセスを404で隠蔽する。

## 4. 重複防止ルール
- 同一グループ内の同名在庫を禁止。
- アプリ側では大文字小文字を無視した重複判定を実施。
- DB側では `unique(family_group_id, name)` 制約を維持。

## 5. エラーレスポンス方針
- 400: バリデーションエラー
- 401: 未認証
- 403: グループ未所属
- 404: 在庫未存在 / 他グループアクセス / 不正ID
- 409: 同名重複
- 500: 想定外エラー

## 6. curl例
```bash
# list
curl http://localhost:8080/api/stock-items \
  -H 'Authorization: Bearer <token>'

# list low stock only
curl 'http://localhost:8080/api/stock-items?lowStockOnly=true' \
  -H 'Authorization: Bearer <token>'

# detail
curl http://localhost:8080/api/stock-items/1 \
  -H 'Authorization: Bearer <token>'

# create
curl -X POST http://localhost:8080/api/stock-items \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"name":"Milk","category":"Dairy","unit":"本","currentStock":2,"minimumStock":1}'

# update
curl -X PUT http://localhost:8080/api/stock-items/1 \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"name":"Milk","category":"Dairy","unit":"本","currentStock":1,"minimumStock":1}'

# logical delete
curl -X DELETE http://localhost:8080/api/stock-items/1 \
  -H 'Authorization: Bearer <token>'
```

## 7. 今回未実装
- 在庫数更新専用API（PATCH /api/stock-items/{id}/stock）
- 自動買い物リスト追加ロジック
- 買い物リストAPI
- 在庫履歴機能
- 一括操作・高度検索

## 8. 次フェーズ候補
- 在庫数更新専用API分離
- 低在庫時の買い物リスト自動追加
- 買い物リスト手動追加/状態更新
- 在庫CRUDの統合テスト追加

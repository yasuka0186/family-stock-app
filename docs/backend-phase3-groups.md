# Backend実装フェーズ3（家族グループAPI実装）

## 1. 目的
- 認証済みユーザーが家族グループを作成・参加・取得できるようにする。
- MVP方針として、1ユーザー1グループ制約をシンプルに保証する。

## 2. 実装API

### POST /api/groups
- 認証: 必須
- 入力: `name`
- バリデーション: 必須, 2〜100文字
- 振る舞い:
  - グループ作成時に招待コード生成
  - 作成者を自動でmembership登録
  - 既に所属済みなら409
- 出力（201）:
```json
{
  "groupId": 10,
  "groupName": "Tanaka Family",
  "inviteCode": "A7Z9M3Q2KD"
}
```

### POST /api/groups/join
- 認証: 必須
- 入力: `inviteCode`
- 振る舞い:
  - 招待コード一致グループへ参加
  - 別グループ所属済みは409
  - 同一グループ二重参加は409
  - 招待コード不正は404
- 出力（200）:
```json
{
  "groupId": 10,
  "groupName": "Tanaka Family",
  "inviteCode": "A7Z9M3Q2KD"
}
```

### GET /api/groups/me
- 認証: 必須
- 振る舞い:
  - 所属あり: joined=true + group情報
  - 未所属: joined=false + group=null
- 出力（所属あり）:
```json
{
  "joined": true,
  "group": {
    "groupId": 10,
    "groupName": "Tanaka Family",
    "inviteCode": "A7Z9M3Q2KD"
  }
}
```

## 3. inviteCode仕様
- 文字種: 英大文字 + 数字（曖昧文字を除外）
- 長さ: 10文字
- 生成方法: `SecureRandom`
- 一意性: 生成後にDB照合し、重複時は再生成

### 採用理由
- 10文字英数字はMVP運用で十分に推測されにくい。
- UUIDより入力しやすく、家族共有ユースケースに向く。

## 4. 1ユーザー1グループ制約
- サービス層で `membership exists by userId` をチェック。
- DB側は `unique(family_group_id, user_id)` で二重所属レコードを防止。
- これによりMVPで必要な所属整合性をシンプルに保証する。

## 5. エラーレスポンス方針
- 400: バリデーションエラー
- 401: 未認証/認証失敗
- 404: 招待コード不正
- 409: 所属済み / 二重参加
- 500: 想定外エラー

## 6. curl例
```bash
# group create
curl -X POST http://localhost:8080/api/groups \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"name":"Tanaka Family"}'

# group join
curl -X POST http://localhost:8080/api/groups/join \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"inviteCode":"A7Z9M3Q2KD"}'

# my group
curl http://localhost:8080/api/groups/me \
  -H 'Authorization: Bearer <token>'
```

## 7. 今回未実装
- 招待コード再発行
- 招待コード有効期限
- 複数グループ所属
- グループ脱退API
- グループ削除API
- 権限管理

## 8. 次フェーズ候補
- 在庫アイテム登録/一覧/更新API
- 低在庫時の買い物リスト自動追加
- 買い物リスト手動追加/状態更新
- 認証・グループAPIの統合テスト追加

# Backend実装フェーズ2（認証API実装）

## 1. 目的
- MVPで必要な認証API（register/login）を最小構成で動かす。
- JWTによる認証状態をフロントで扱えるようにする。

## 2. 今回実装したAPI

### POST /api/auth/register
- 目的: 新規ユーザー登録
- 認証: 不要
- 入力:
  - `name` (必須)
  - `email` (必須, email形式)
  - `password` (必須, 8文字以上)
- 振る舞い:
  - email重複時は409
  - パスワードはBCryptハッシュ化して保存
- 出力（201）:
```json
{
  "userId": 1,
  "message": "registered"
}
```

### POST /api/auth/login
- 目的: ログイン認証
- 認証: 不要
- 入力:
  - `email`
  - `password`
- 振る舞い:
  - 成功時はJWTを返す
  - 失敗時は401（詳細を出しすぎない）
- 出力（200）:
```json
{
  "accessToken": "<jwt>",
  "user": {
    "id": 1,
    "name": "Taro",
    "email": "taro@example.com"
  }
}
```

### GET /api/auth/me
- 目的: ログイン中ユーザーの再取得
- 認証: 必要（Bearer Token）
- 採用理由:
  - MVPフロントで「トークン復元後にユーザー表示」を簡単にするため
  - 実装コストが小さく、認証状態の検証APIとして有用
- 出力（200）:
```json
{
  "id": 1,
  "name": "Taro",
  "email": "taro@example.com"
}
```

## 3. 認証フロー概要
1. `register` でユーザー作成（この時点ではJWTを返さない）
2. `login` で認証し、`accessToken` を取得
3. フロントは `Authorization: Bearer <token>` で保護APIへアクセス
4. 初期表示時は `GET /api/auth/me` でユーザー情報を復元

### registerでJWTを返さない理由
- MVPでも「登録」と「ログイン」を分離した方が、失敗時の責務が明確でデバッグしやすい。
- 後続でメール認証等を入れる場合にも拡張しやすい。

## 4. JWTの扱い
- subject: ユーザーemail
- 署名鍵: `app.jwt.secret`（環境変数で上書き）
- 有効期限: `app.jwt.access-token-ttl-seconds`
- 未認証アクセスは401 JSONで返却

## 5. エラーレスポンス方針
共通形式:
- `timestamp`
- `status`
- `error`
- `message`
- `path`
- `validationErrors`（バリデーション時のみ）

主なエラー:
- 400: バリデーションエラー
- 401: 認証失敗（invalid email or password）
- 409: email重複
- 500: 想定外エラー

## 6. curl例
```bash
# register
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"name":"Taro","email":"taro@example.com","password":"password123"}'

# login
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"taro@example.com","password":"password123"}'

# me
curl http://localhost:8080/api/auth/me \
  -H 'Authorization: Bearer <accessToken>'
```

## 7. 今回未実装
- リフレッシュトークン
- パスワード再発行
- メール認証
- 多要素認証
- ロールベース認可

## 8. 次フェーズ候補
- グループ作成/参加API
- 在庫CRUD
- 在庫更新時の買い物リスト自動追加
- 認証APIの統合テスト追加

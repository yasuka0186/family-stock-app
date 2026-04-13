package com.familystock.backend.exception;

import com.familystock.backend.dto.response.ErrorResponse;
import com.familystock.backend.exception.auth.DuplicateEmailException;
import com.familystock.backend.exception.auth.InvalidCredentialsException;
import com.familystock.backend.exception.group.AlreadyInGroupException;
import com.familystock.backend.exception.group.AlreadyMemberException;
import com.familystock.backend.exception.group.InvalidInviteCodeException;
import com.familystock.backend.exception.shopping.InvalidShoppingListRequestException;
import com.familystock.backend.exception.shopping.ShoppingListItemNotFoundException;
import com.familystock.backend.exception.stock.DuplicateStockItemException;
import com.familystock.backend.exception.stock.GroupMembershipRequiredException;
import com.familystock.backend.exception.stock.InvalidStockUpdateOperationException;
import com.familystock.backend.exception.stock.StockItemNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * API全体の例外レスポンスを統一するハンドラ。
 * 実装初期からエラー形式を固定し、フロントとの結合を安定させる。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * バリデーションエラーを400レスポンスとして返す。
     *
     * @param ex バリデーション例外
     * @param request リクエスト情報
     * @return 統一形式のバリデーションエラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * メール重複エラーを409で返す。
     * フロントはこのエラーを使って再入力ガイドを表示する想定。
     *
     * @param ex メール重複例外
     * @param request リクエスト情報
     * @return 統一形式の競合エラーレスポンス
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
            DuplicateEmailException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 認証失敗時のエラーを401で返す。
     * セキュリティ上、認証失敗理由は詳細化しない。
     *
     * @param ex 認証失敗例外
     * @param request リクエスト情報
     * @return 統一形式の認証エラーレスポンス
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 1ユーザー1グループ制約違反を409で返す。
     * フロントはこのエラーを受けて所属済み画面へ誘導する想定。
     *
     * @param ex 所属済み例外
     * @param request リクエスト情報
     * @return 統一形式の競合エラーレスポンス
     */
    @ExceptionHandler(AlreadyInGroupException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyInGroupException(
            AlreadyInGroupException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 同一グループ二重参加を409で返す。
     *
     * @param ex 二重参加例外
     * @param request リクエスト情報
     * @return 統一形式の競合エラーレスポンス
     */
    @ExceptionHandler(AlreadyMemberException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyMemberException(
            AlreadyMemberException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 招待コード不正を404で返す。
     * フロントはコード再入力ガイドに利用できる。
     *
     * @param ex 招待コード不正例外
     * @param request リクエスト情報
     * @return 統一形式の未検出エラーレスポンス
     */
    @ExceptionHandler(InvalidInviteCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInviteCodeException(
            InvalidInviteCodeException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * グループ未所属ユーザーの在庫操作を403で返す。
     * 在庫はグループ単位のため、未所属状態でのアクセスを明示的に拒否する。
     *
     * @param ex グループ未所属例外
     * @param request リクエスト情報
     * @return 統一形式の認可エラーレスポンス
     */
    @ExceptionHandler(GroupMembershipRequiredException.class)
    public ResponseEntity<ErrorResponse> handleGroupMembershipRequiredException(
            GroupMembershipRequiredException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 在庫未存在エラーを404で返す。
     * 他グループ越境アクセスも同じ404に寄せ、情報漏えいを防ぐ。
     *
     * @param ex 在庫未存在例外
     * @param request リクエスト情報
     * @return 統一形式の未検出エラーレスポンス
     */
    @ExceptionHandler(StockItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStockItemNotFoundException(
            StockItemNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 同名在庫重複エラーを409で返す。
     * フロントは同名入力の見直し案内に利用できる。
     *
     * @param ex 同名重複例外
     * @param request リクエスト情報
     * @return 統一形式の競合エラーレスポンス
     */
    @ExceptionHandler(DuplicateStockItemException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateStockItemException(
            DuplicateStockItemException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 不正な在庫更新操作を400で返す。
     * フロントは入力値再確認を促すUIへ遷移できる。
     *
     * @param ex 在庫更新不正例外
     * @param request リクエスト情報
     * @return 統一形式のバッドリクエストレスポンス
     */
    @ExceptionHandler(InvalidStockUpdateOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStockUpdateOperationException(
            InvalidStockUpdateOperationException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 買い物リストの業務バリデーションエラーを400で返す。
     * フロントは入力補正メッセージとして扱える。
     *
     * @param ex 買い物リスト入力不正例外
     * @param request リクエスト情報
     * @return 統一形式のバッドリクエストレスポンス
     */
    @ExceptionHandler(InvalidShoppingListRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidShoppingListRequestException(
            InvalidShoppingListRequestException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 買い物リスト項目未存在エラーを404で返す。
     * 他グループ境界アクセス時も同一応答にして情報露出を抑える。
     *
     * @param ex 買い物リスト未存在例外
     * @param request リクエスト情報
     * @return 統一形式の未検出レスポンス
     */
    @ExceptionHandler(ShoppingListItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleShoppingListItemNotFoundException(
            ShoppingListItemNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 想定外エラーを500レスポンスとして返す。
     *
     * @param ex 想定外例外
     * @param request リクエスト情報
     * @return 統一形式のサーバーエラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("unexpected server error")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

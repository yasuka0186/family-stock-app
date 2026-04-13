package com.familystock.backend.service.stock.impl;

import com.familystock.backend.domain.entity.User;
import com.familystock.backend.domain.entity.group.FamilyGroup;
import com.familystock.backend.domain.entity.group.FamilyMembership;
import com.familystock.backend.domain.entity.stock.StockItem;
import com.familystock.backend.domain.entity.shopping.ShoppingListItem;
import com.familystock.backend.domain.entity.shopping.ShoppingListItem;
import com.familystock.backend.domain.entity.shopping.type.ShoppingListSourceType;
import com.familystock.backend.domain.entity.shopping.type.ShoppingListStatus;
import com.familystock.backend.domain.entity.stock.StockItem;
import com.familystock.backend.dto.request.StockUpdateRequest;
import com.familystock.backend.dto.request.stock.StockItemUpsertRequest;
import com.familystock.backend.dto.response.stock.StockItemResponse;
import com.familystock.backend.exception.auth.InvalidCredentialsException;
import com.familystock.backend.exception.stock.DuplicateStockItemException;
import com.familystock.backend.exception.stock.GroupMembershipRequiredException;
import com.familystock.backend.exception.stock.StockItemNotFoundException;
import com.familystock.backend.repository.UserRepository;
import com.familystock.backend.repository.group.FamilyMembershipRepository;
import com.familystock.backend.exception.stock.InvalidStockUpdateOperationException;
import com.familystock.backend.exception.stock.StockItemNotFoundException;
import com.familystock.backend.repository.UserRepository;
import com.familystock.backend.repository.group.FamilyMembershipRepository;
import com.familystock.backend.repository.shopping.ShoppingListItemRepository;
import com.familystock.backend.repository.stock.StockItemRepository;
import com.familystock.backend.service.stock.StockItemService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 在庫アイテムCRUDの業務ロジック実装。
 * 所属グループ境界をサービス層で統一チェックし、他グループアクセスを遮断する。
 */
@Service
@RequiredArgsConstructor
public class StockItemServiceImpl implements StockItemService {

    private final UserRepository userRepository;
    private final FamilyMembershipRepository familyMembershipRepository;
    private final StockItemRepository stockItemRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;

    /**
     * 所属グループの在庫一覧を取得する。
     * MVPでは名前昇順を採用し、並びが安定して見やすい一覧を提供する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param lowStockOnly 低在庫のみ取得するか
     * @return 在庫一覧
     */
    @Override
    @Transactional(readOnly = true)
    public List<StockItemResponse> getStockItems(String userEmail, boolean lowStockOnly) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        return stockItemRepository.findAllByFamilyGroupIdAndIsActiveTrueOrderByNameAsc(familyGroup.getId())
                .stream()
                .filter(item -> !lowStockOnly || isLowStock(item))
                .map(this::toResponse)
                .toList();
    }

    /**
     * 所属グループ内の在庫詳細を取得する。
     * IDと所属グループの両方で検索し、他グループ情報を見せない。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param stockItemId 在庫ID
     * @return 在庫詳細
     */
    @Override
    @Transactional(readOnly = true)
    public StockItemResponse getStockItem(String userEmail, Long stockItemId) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        StockItem stockItem = stockItemRepository.findByIdAndFamilyGroupIdAndIsActiveTrue(stockItemId, familyGroup.getId())
                .orElseThrow(() -> new StockItemNotFoundException("stock item not found"));

        return toResponse(stockItem);
    }

    /**
     * 所属グループに在庫アイテムを作成する。
     * 同名重複を防ぐことで、在庫判定や将来の自動追加ロジックを安定化する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param request 作成入力
     * @return 作成後在庫情報
     */
    @Override
    @Transactional
    public StockItemResponse createStockItem(String userEmail, StockItemUpsertRequest request) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        String normalizedName = normalizeName(request.getName());
        ensureNoDuplicateName(familyGroup.getId(), normalizedName);

        StockItem stockItem = new StockItem();
        stockItem.setFamilyGroup(familyGroup);
        stockItem.setName(request.getName().trim());
        stockItem.setCategory(normalizeOptionalText(request.getCategory()));
        stockItem.setUnit(request.getUnit().trim());
        stockItem.setCurrentStock(request.getCurrentStock());
        stockItem.setMinimumStock(request.getMinimumStock());
        stockItem.setIsActive(true);
        stockItem.setCreatedBy(user);
        stockItem.setCreatedAt(LocalDateTime.now());
        stockItem.setUpdatedAt(LocalDateTime.now());

        StockItem saved = stockItemRepository.save(stockItem);
        return toResponse(saved);
    }

    /**
     * 所属グループ内の在庫アイテムを更新する。
     * ID+グループ境界で対象を限定し、越境更新を防止する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param stockItemId 更新対象ID
     * @param request 更新入力
     * @return 更新後在庫情報
     */
    @Override
    @Transactional
    public StockItemResponse updateStockItem(String userEmail, Long stockItemId, StockItemUpsertRequest request) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        StockItem stockItem = stockItemRepository.findByIdAndFamilyGroupIdAndIsActiveTrue(stockItemId, familyGroup.getId())
                .orElseThrow(() -> new StockItemNotFoundException("stock item not found"));

        String normalizedName = normalizeName(request.getName());
        ensureNoDuplicateNameExcludingSelf(familyGroup.getId(), normalizedName, stockItemId);

        stockItem.setName(request.getName().trim());
        stockItem.setCategory(normalizeOptionalText(request.getCategory()));
        stockItem.setUnit(request.getUnit().trim());
        stockItem.setCurrentStock(request.getCurrentStock());
        stockItem.setMinimumStock(request.getMinimumStock());
        stockItem.setUpdatedAt(LocalDateTime.now());

        return toResponse(stockItem);
    }

    /**
     * 在庫アイテムを論理削除する。
     * MVPでは物理削除を避け、将来の履歴/監査拡張を壊さないようにする。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param stockItemId 削除対象ID
     */
    @Override
    @Transactional
    public void deleteStockItem(String userEmail, Long stockItemId) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        StockItem stockItem = stockItemRepository.findByIdAndFamilyGroupIdAndIsActiveTrue(stockItemId, familyGroup.getId())
                .orElseThrow(() -> new StockItemNotFoundException("stock item not found"));

        stockItem.setIsActive(false);
        stockItem.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 在庫数を更新する専用処理。
     * CRUD更新と責務を分離し、数量変動に伴う低在庫判定ロジックを集中管理する。
     *
     * @param userEmail JWT subjectとして扱うユーザーメール
     * @param stockItemId 更新対象在庫ID
     * @param request 更新モード・数量・理由
     * @return 更新後在庫情報
     */
    @Override
    @Transactional
    public StockItemResponse updateStockQuantity(String userEmail, Long stockItemId, StockUpdateRequest request) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        StockItem stockItem = stockItemRepository.findByIdAndFamilyGroupIdAndIsActiveTrue(stockItemId, familyGroup.getId())
                .orElseThrow(() -> new StockItemNotFoundException("stock item not found"));

        int updatedStock = calculateUpdatedStock(stockItem.getCurrentStock(), request);
        stockItem.setCurrentStock(updatedStock);
        stockItem.setUpdatedAt(LocalDateTime.now());

        // TODO: 後続フェーズで在庫履歴テーブルを導入し、reasonを履歴保存する。
        // MVPではAPI互換を先に確保し、保存処理は最小化する。

        if (isLowStock(stockItem)) {
            addShoppingListItemIfMissing(stockItem, user);
        }
        // 在庫回復時に自動クローズしない方針:
        // PENDINGの購買判断はユーザー主導にし、意図しない削除を防ぐ。

        return toResponse(stockItem);
    }

    /**
     * 認証ユーザーを取得する。
     *
     * @param userEmail JWT subjectとして扱うメール
     * @return ユーザー
     */
    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("user not found"));
    }

    /**
     * ユーザー所属グループを取得する。
     * 在庫はグループ単位データなので、未所属ユーザーの操作は拒否する。
     *
     * @param userId ユーザーID
     * @return 所属グループ
     */
    private FamilyGroup findUserGroupOrThrow(Long userId) {
        FamilyMembership membership = familyMembershipRepository.findByUserId(userId)
                .orElseThrow(() -> new GroupMembershipRequiredException("group membership required"));
        return membership.getFamilyGroup();
    }

    /**
     * 同一グループ内の同名重複を検証する。
     *
     * @param familyGroupId 所属グループID
     * @param normalizedName 正規化済み在庫名
     */
    private void ensureNoDuplicateName(Long familyGroupId, String normalizedName) {
        if (stockItemRepository.existsActiveByFamilyGroupIdAndNormalizedName(familyGroupId, normalizedName)) {
            throw new DuplicateStockItemException("stock item name already exists in group");
        }
    }

    /**
     * 更新時に自分を除外して同名重複を検証する。
     *
     * @param familyGroupId 所属グループID
     * @param normalizedName 正規化済み在庫名
     * @param stockItemId 更新対象ID
     */
    private void ensureNoDuplicateNameExcludingSelf(Long familyGroupId, String normalizedName, Long stockItemId) {
        if (stockItemRepository.existsActiveByFamilyGroupIdAndNormalizedNameExcludingId(
                familyGroupId,
                normalizedName,
                stockItemId
        )) {
            throw new DuplicateStockItemException("stock item name already exists in group");
        }
    }

    /**
     * 在庫名の比較用正規化を行う。
     *
     * @param rawName 入力名
     * @return 小文字化済み名称
     */
    private String normalizeName(String rawName) {
        return rawName.trim().toLowerCase();
    }

    /**
     * 任意文字列の空白をnullへ正規化する。
     *
     * @param value 任意入力
     * @return 正規化済み文字列
     */
    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * 最低在庫判定を返す。
     * TODO: 在庫更新専用API導入時に、この判定を買い物リスト自動追加ロジックへ分離する。
     *
     * @param stockItem 判定対象
     * @return 低在庫ならtrue
     */
    private boolean isLowStock(StockItem stockItem) {
        return stockItem.getCurrentStock() <= stockItem.getMinimumStock();
    }

    /**
     * 更新モードに応じて更新後在庫数を計算する。
     * SUBTRACTで負数になるケースは業務上不正として拒否する。
     *
     * @param currentStock 現在在庫
     * @param request 更新要求
     * @return 更新後在庫
     */
    private int calculateUpdatedStock(int currentStock, StockUpdateRequest request) {
        return switch (request.getMode()) {
            case SET -> request.getQuantity();
            case ADD -> currentStock + request.getQuantity();
            case SUBTRACT -> {
                int result = currentStock - request.getQuantity();
                if (result < 0) {
                    throw new InvalidStockUpdateOperationException("stock cannot be negative");
                }
                yield result;
            }
        };
    }

    /**
     * 低在庫時に買い物リストへ自動追加する。
     * 既存PENDINGがある場合は重複追加せず何もしない。
     *
     * @param stockItem 判定対象在庫
     * @param user 操作ユーザー
     */
    private void addShoppingListItemIfMissing(StockItem stockItem, User user) {
        boolean existsPending = shoppingListItemRepository.existsPendingByFamilyGroupIdAndStockItemId(
                stockItem.getFamilyGroup().getId(),
                stockItem.getId()
        );
        if (existsPending) {
            return;
        }

        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.setFamilyGroup(stockItem.getFamilyGroup());
        shoppingListItem.setStockItem(stockItem);
        shoppingListItem.setNameSnapshot(stockItem.getName());
        shoppingListItem.setUnitSnapshot(stockItem.getUnit());
        shoppingListItem.setStatus("PENDING");
        shoppingListItem.setSourceType("AUTO_LOW_STOCK");
        shoppingListItem.setStatus(ShoppingListStatus.PENDING.name());
        shoppingListItem.setSourceType(ShoppingListSourceType.AUTO_LOW_STOCK.name());
        shoppingListItem.setCreatedBy(user);
        shoppingListItem.setCreatedAt(LocalDateTime.now());
        shoppingListItem.setUpdatedAt(LocalDateTime.now());

        shoppingListItemRepository.save(shoppingListItem);
    }

    /**
     * エンティティをAPI返却DTOへ変換する。
     *
     * @param stockItem 変換元在庫
     * @return 返却用DTO
     */
    private StockItemResponse toResponse(StockItem stockItem) {
        return StockItemResponse.builder()
                .id(stockItem.getId())
                .familyGroupId(stockItem.getFamilyGroup().getId())
                .name(stockItem.getName())
                .category(stockItem.getCategory())
                .unit(stockItem.getUnit())
                .currentStock(stockItem.getCurrentStock())
                .minimumStock(stockItem.getMinimumStock())
                .lowStock(isLowStock(stockItem))
                .createdByEmail(stockItem.getCreatedBy() != null ? stockItem.getCreatedBy().getEmail() : null)
                .build();
    }
}

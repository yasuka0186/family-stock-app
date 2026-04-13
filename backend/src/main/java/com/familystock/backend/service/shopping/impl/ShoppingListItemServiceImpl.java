package com.familystock.backend.service.shopping.impl;

import com.familystock.backend.domain.entity.User;
import com.familystock.backend.domain.entity.group.FamilyGroup;
import com.familystock.backend.domain.entity.group.FamilyMembership;
import com.familystock.backend.domain.entity.shopping.ShoppingListItem;
import com.familystock.backend.domain.entity.shopping.type.ShoppingListSourceType;
import com.familystock.backend.domain.entity.shopping.type.ShoppingListStatus;
import com.familystock.backend.domain.entity.stock.StockItem;
import com.familystock.backend.dto.request.shopping.ShoppingListItemCreateRequest;
import com.familystock.backend.dto.response.shopping.ShoppingListItemCreateResponse;
import com.familystock.backend.dto.response.shopping.ShoppingListItemResponse;
import com.familystock.backend.exception.auth.InvalidCredentialsException;
import com.familystock.backend.exception.shopping.InvalidShoppingListRequestException;
import com.familystock.backend.exception.shopping.ShoppingListItemNotFoundException;
import com.familystock.backend.exception.stock.GroupMembershipRequiredException;
import com.familystock.backend.exception.stock.StockItemNotFoundException;
import com.familystock.backend.repository.UserRepository;
import com.familystock.backend.repository.group.FamilyMembershipRepository;
import com.familystock.backend.repository.shopping.ShoppingListItemRepository;
import com.familystock.backend.repository.stock.StockItemRepository;
import com.familystock.backend.service.shopping.ShoppingListItemService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 買い物リストAPIの業務ロジック実装。
 * 手動追加と自動追加の整合性を壊さないことを最優先にする。
 */
@Service
@RequiredArgsConstructor
public class ShoppingListItemServiceImpl implements ShoppingListItemService {

    private final UserRepository userRepository;
    private final FamilyMembershipRepository familyMembershipRepository;
    private final StockItemRepository stockItemRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;

    /**
     * 買い物リスト一覧を取得する。
     * デフォルトはPENDINGのみ返し、日常の買い物確認を最短で行えるようにする。
     *
     * @param userEmail JWT subjectとして扱うメール
     * @param status optionalの状態フィルタ
     * @return 買い物リスト一覧
     */
    @Override
    @Transactional(readOnly = true)
    public List<ShoppingListItemResponse> getShoppingListItems(String userEmail, ShoppingListStatus status) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        List<ShoppingListItem> items;
        if (status == null) {
            items = shoppingListItemRepository.findByFamilyGroupIdAndStatusOrderByCreatedAtDesc(
                    familyGroup.getId(),
                    ShoppingListStatus.PENDING.name()
            );
        } else {
            items = shoppingListItemRepository.findByFamilyGroupIdAndStatusOrderByCreatedAtDesc(
                    familyGroup.getId(),
                    status.name()
            );
        }

        return items.stream().map(this::toResponse).toList();
    }

    /**
     * 買い物リストへ手動追加する。
     * stockItemIdあり/なしで扱いを分けることで、既存在庫起点と自由入力起点を両立する。
     *
     * @param userEmail JWT subjectとして扱うメール
     * @param request 手動追加入力
     * @return 作成結果（重複時は既存返却）
     */
    @Override
    @Transactional
    public ShoppingListItemCreateResponse createManualItem(String userEmail, ShoppingListItemCreateRequest request) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        if (request.getStockItemId() != null) {
            StockItem stockItem = stockItemRepository.findByIdAndFamilyGroupIdAndIsActiveTrue(
                            request.getStockItemId(),
                            familyGroup.getId()
                    )
                    .orElseThrow(() -> new StockItemNotFoundException("stock item not found"));

            // 自動追加(AUTO)と手動追加(MANUAL)の重複PENDINGを共通で抑止する。
            ShoppingListItem existing = shoppingListItemRepository
                    .findFirstByFamilyGroupIdAndStockItemIdAndStatusOrderByCreatedAtDesc(
                            familyGroup.getId(),
                            stockItem.getId(),
                            ShoppingListStatus.PENDING.name()
                    )
                    .orElse(null);
            if (existing != null) {
                return ShoppingListItemCreateResponse.builder()
                        .created(false)
                        .item(toResponse(existing))
                        .build();
            }

            ShoppingListItem created = new ShoppingListItem();
            created.setFamilyGroup(familyGroup);
            created.setStockItem(stockItem);
            created.setNameSnapshot(stockItem.getName());
            created.setUnitSnapshot(stockItem.getUnit());
            created.setStatus(ShoppingListStatus.PENDING.name());
            created.setSourceType(ShoppingListSourceType.MANUAL.name());
            created.setNote(normalizeOptionalText(request.getNote()));
            created.setCreatedBy(user);
            created.setCreatedAt(LocalDateTime.now());
            created.setUpdatedAt(LocalDateTime.now());

            ShoppingListItem saved = shoppingListItemRepository.save(created);
            return ShoppingListItemCreateResponse.builder()
                    .created(true)
                    .item(toResponse(saved))
                    .build();
        }

        // 自由入力は在庫マスタ非依存のメモ用途として扱うため、同名重複を許容する。
        if (isBlank(request.getName()) || isBlank(request.getUnit())) {
            throw new InvalidShoppingListRequestException("name and unit are required when stockItemId is null");
        }

        ShoppingListItem created = new ShoppingListItem();
        created.setFamilyGroup(familyGroup);
        created.setStockItem(null);
        created.setNameSnapshot(request.getName().trim());
        created.setUnitSnapshot(request.getUnit().trim());
        created.setStatus(ShoppingListStatus.PENDING.name());
        created.setSourceType(ShoppingListSourceType.MANUAL.name());
        created.setNote(normalizeOptionalText(request.getNote()));
        created.setCreatedBy(user);
        created.setCreatedAt(LocalDateTime.now());
        created.setUpdatedAt(LocalDateTime.now());

        ShoppingListItem saved = shoppingListItemRepository.save(created);
        return ShoppingListItemCreateResponse.builder()
                .created(true)
                .item(toResponse(saved))
                .build();
    }

    /**
     * 買い物リスト状態を更新する。
     * MVPでは再オープン（BOUGHT/SKIPPED -> PENDING）を許可し、運用上の修正コストを下げる。
     *
     * @param userEmail JWT subjectとして扱うメール
     * @param itemId 更新対象ID
     * @param status 更新先状態
     * @return 更新後項目
     */
    @Override
    @Transactional
    public ShoppingListItemResponse updateStatus(String userEmail, Long itemId, ShoppingListStatus status) {
        User user = findUserByEmail(userEmail);
        FamilyGroup familyGroup = findUserGroupOrThrow(user.getId());

        ShoppingListItem item = shoppingListItemRepository.findByIdAndFamilyGroupId(itemId, familyGroup.getId())
                .orElseThrow(() -> new ShoppingListItemNotFoundException("shopping list item not found"));

        item.setStatus(status.name());
        item.setUpdatedAt(LocalDateTime.now());

        return toResponse(item);
    }

    /**
     * 認証ユーザーを取得する。
     *
     * @param email JWT subjectとして扱うメール
     * @return ユーザー
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("user not found"));
    }

    /**
     * ユーザー所属グループを取得する。
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
     * Entityを返却DTOへ変換する。
     *
     * @param item 変換元項目
     * @return API返却DTO
     */
    private ShoppingListItemResponse toResponse(ShoppingListItem item) {
        return ShoppingListItemResponse.builder()
                .id(item.getId())
                .stockItemId(item.getStockItem() != null ? item.getStockItem().getId() : null)
                .name(item.getNameSnapshot())
                .unit(item.getUnitSnapshot())
                .status(ShoppingListStatus.valueOf(item.getStatus()))
                .sourceType(ShoppingListSourceType.valueOf(item.getSourceType()))
                .note(item.getNote())
                .createdAt(item.getCreatedAt())
                .build();
    }

    /**
     * 任意文字列の空白をnullへ正規化する。
     *
     * @param value 任意入力
     * @return 正規化後文字列
     */
    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * 空文字判定ヘルパー。
     *
     * @param value 判定対象
     * @return 空文字またはnullならtrue
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

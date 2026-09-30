package com.payflow.api.dto.response;

import com.payflow.api.domain.entity.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse(
    Long walletId,
    Long userId,
    String userName,
    BigDecimal balance,
    LocalDateTime updatedAt
) {
    public static WalletResponse fromEntity(Wallet wallet) {
        return new WalletResponse(
            wallet.getId(),
            wallet.getUser().getId(),
            wallet.getUser().getFullName(),
            wallet.getBalance(),
            wallet.getUpdatedAt()
        );
    }
}

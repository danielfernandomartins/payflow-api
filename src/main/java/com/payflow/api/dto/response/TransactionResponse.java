package com.payflow.api.dto.response;

import com.payflow.api.domain.entity.Transaction;
import com.payflow.api.domain.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    Long payerId,
    String payerName,
    Long payeeId,
    String payeeName,
    BigDecimal amount,
    TransactionStatus status,
    LocalDateTime createdAt
) {
    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getPayer().getId(),
            transaction.getPayer().getFullName(),
            transaction.getPayee().getId(),
            transaction.getPayee().getFullName(),
            transaction.getAmount(),
            transaction.getStatus(),
            transaction.getCreatedAt()
        );
    }
}

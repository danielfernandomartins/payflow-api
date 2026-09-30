package com.payflow.api.dto.response;

import com.payflow.api.domain.entity.Transaction;
import com.payflow.api.domain.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
    Long transactionId,
    Long payerId,
    String payerName,
    Long payeeId,
    String payeeName,
    BigDecimal amount,
    TransactionStatus status,
    String message,
    LocalDateTime timestamp
) {
    public static TransferResponse fromEntity(Transaction transaction, String message) {
        return new TransferResponse(
            transaction.getId(),
            transaction.getPayer().getId(),
            transaction.getPayer().getFullName(),
            transaction.getPayee().getId(),
            transaction.getPayee().getFullName(),
            transaction.getAmount(),
            transaction.getStatus(),
            message,
            transaction.getCreatedAt()
        );
    }
}

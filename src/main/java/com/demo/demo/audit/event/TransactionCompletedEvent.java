package com.demo.demo.audit.event;

import com.demo.demo.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCompletedEvent(
        UUID eventId,
        String referenceId,
        TransactionType transactionType,
        String accountNumber,
        String counterpartyAccountNumber,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        LocalDateTime completedAt) {
}

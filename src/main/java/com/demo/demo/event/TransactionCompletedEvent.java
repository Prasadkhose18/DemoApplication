package com.demo.demo.event;

import com.demo.demo.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable transaction notification payload. It deliberately contains no
 * JPA entities, allowing notification delivery to remain decoupled from the
 * banking domain and persistence context.
 */
public record TransactionCompletedEvent(
        Long transactionId,
        String referenceId,
        String accountNumber,
        String accountType,
        String customerName,
        String customerEmail,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        LocalDateTime transactionTime) {
}

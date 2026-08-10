package com.demo.demo.event;

import com.demo.demo.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


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

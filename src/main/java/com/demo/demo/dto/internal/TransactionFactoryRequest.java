package com.demo.demo.dto.internal;

import com.demo.demo.entity.Accounts;
import com.demo.demo.enums.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionFactoryRequest(
        Accounts account,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String referenceId) {
}

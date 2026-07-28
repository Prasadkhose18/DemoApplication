package com.demo.demo.dto.internal;

import com.demo.demo.entity.Accounts;
import com.demo.demo.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TransactionFactoryRequest {

    private Accounts account;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String referenceId;
}
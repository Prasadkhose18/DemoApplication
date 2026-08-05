package com.demo.demo.dto.response;

import com.demo.demo.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAuditDTO {

    private Long transactionId;

    private String referenceId;

    private String customerName;

    private String accountNumber;

    private String accountType;

    private TransactionType transactionType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private LocalDateTime transactionTime;

}
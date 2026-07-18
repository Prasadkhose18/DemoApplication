package com.demo.demo.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class TransactionResponseDTO {

    private String accountNumber;

    private String transactionType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private String referenceId;

    private LocalDateTime transactionTime;




}

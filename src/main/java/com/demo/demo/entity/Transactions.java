package com.demo.demo.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id",nullable = false)
    private Accounts account;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "refereance_id", nullable = false)
    private String referenceId;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "balance_before", nullable = false)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;
}





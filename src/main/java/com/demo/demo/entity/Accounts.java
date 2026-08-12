package com.demo.demo.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Table(name = "Accounts")
@Entity
@Data
public class Accounts {

    @Id
    @GeneratedValue
    @Column(name = "Account_Id")
    private Long accountId;

    @Column(name = "Account_Number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "Account_Type",nullable = false)
    private String accountType;

    @Column(name = "Balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "Status", nullable = false)
    private String status;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "userid",nullable = false)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transactions> transactions;
}

package com.demo.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "Accounts")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid",nullable = false)
    private User user;
}

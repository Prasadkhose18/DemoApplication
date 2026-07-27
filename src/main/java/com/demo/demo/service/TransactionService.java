package com.demo.demo.service;

import com.demo.demo.dto.BalanceResponseDTO;
import com.demo.demo.dto.TransferRequestDTO;
import com.demo.demo.entity.Transactions;
import com.demo.demo.model.TransferResult;

import java.math.BigDecimal;

public interface TransactionService {

    Transactions deposit(String accountNumber, BigDecimal amount);

    Transactions withdraw(String accountNumber, BigDecimal amount);

    BigDecimal checkBalance(String accountNumber);

    BalanceResponseDTO getBalance(String accountNumber);

    TransferResult transfer(TransferRequestDTO request);
}

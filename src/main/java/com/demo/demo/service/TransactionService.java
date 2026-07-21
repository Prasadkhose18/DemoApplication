package com.demo.demo.service;


import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.exception.InsufficientBalanceException;
import com.demo.demo.exception.InvalidTransactionException;
import com.demo.demo.exception.ResourceNotFoundException;
import com.demo.demo.factory.TransactionFactory;
import com.demo.demo.repository.AccountRepository;
import com.demo.demo.repository.TransactionRepository;
import com.demo.demo.security.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionFactory transactionFactory;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionReposiory, TransactionFactory transactionFactory) {
        this.accountRepository = accountRepository;
        this.transactionFactory = transactionFactory;
    }

    @Transactional
    public Transactions deposit(String accountNumber, BigDecimal amount){

        if(amount.compareTo(BigDecimal.ZERO)<=0)
            throw new InvalidTransactionException("Amount must be greater than zero");

        Accounts account = validateOwnership(accountNumber);

        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(amount);

        account.setBalance(after);
        accountRepository.save(account);

        System.out.println("Account Saved");

        Transactions txn = transactionFactory.create(
                account,"DEPOSIT",amount,before,after
        );


        return txn;
    }

public Transactions withdraw(String accountNumber, BigDecimal amount){
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidTransactionException("Withdraw amount must be positive");
    }

    Accounts account = validateOwnership(accountNumber);

    if (account.getBalance().compareTo(amount) < 0) {
        throw new InsufficientBalanceException("Insufficient balance");
    }

    BigDecimal before = account.getBalance();
    BigDecimal after = before.subtract(amount);

    account.setBalance(after);
    System.out.println("Transaction done and now saving into repo");
    accountRepository.save(account);
    System.out.println("Saved txn into repo -> going to factory for account");

    Transactions txn = transactionFactory.create(
            account,"WITHDRAW",amount,before,after
    );

    return txn;
}


    private Accounts validateOwnership(String accountNumber) {

        Accounts account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        String currentUserEmail = SecurityUtil.getCurrentUserEmail();

        if (!account.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Unauthorized account access");
        }

        return account;
    }
}

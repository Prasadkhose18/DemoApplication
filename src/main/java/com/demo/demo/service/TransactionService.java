package com.demo.demo.service;


import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.exception.InsufficientBalanceException;
import com.demo.demo.exception.InvalidTransactionException;
import com.demo.demo.exception.ResourceNotFoundException;
import com.demo.demo.repository.AccountRepository;
import com.demo.demo.repository.TransactionReposiory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionReposiory transactionReposiory;

    public TransactionService(AccountRepository accountRepository, TransactionReposiory transactionReposiory) {
        this.accountRepository = accountRepository;
        this.transactionReposiory = transactionReposiory;
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

        Transactions txn = new Transactions();

        System.out.println("Transaction created");

        txn.setAccount(account);
        txn.setTransactionType("DEPOSIT");
        txn.setAmount(amount);
        txn.setBalanceBefore(before);
        txn.setBalanceAfter(after);
        txn.setTransactionTime(LocalDateTime.now());
        txn.setReferenceId(generateReferenceId());
        return transactionReposiory.save(txn);


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


    Transactions txn = new Transactions();

    txn.setAccount(account);
    txn.setTransactionType("WITHDRAW");
    txn.setAmount(amount);
    txn.setBalanceBefore(before);
    txn.setBalanceAfter(after);
    txn.setReferenceId(generateReferenceId());
    txn.setTransactionTime(LocalDateTime.now());

    return transactionReposiory.save(txn);
}



    private String generateReferenceId() {
        return "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();

    }


    private Accounts validateOwnership(String accountNumber) {

        Accounts account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        String currentUserEmail = "prasad@example.com";

        if (!account.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Unauthorized account access");
        }

        return account;
    }


}

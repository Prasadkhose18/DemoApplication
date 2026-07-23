package com.demo.demo.service;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.entity.User;
import com.demo.demo.exception.InsufficientBalanceException;
import com.demo.demo.exception.InvalidTransactionException;
import com.demo.demo.exception.ResourceNotFoundException;
import com.demo.demo.exception.UnauthorizedAccessException;
import com.demo.demo.factory.TransactionFactory;
import com.demo.demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionFactory transactionFactory;
    private final CurrentUserService currentUserService;

    public TransactionService(AccountRepository accountRepository,
                              TransactionFactory transactionFactory,
                              CurrentUserService currentUserService) {
        this.accountRepository = accountRepository;
        this.transactionFactory = transactionFactory;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public Transactions deposit(String accountNumber, BigDecimal amount) {

        log.info("Deposit request received. Account: {}, Amount: {}", accountNumber, amount);

        validateAmount(amount);

        Accounts account = validateOwnership(accountNumber);

        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(amount);

        updateBalance(account, after);

        Transactions transaction = transactionFactory.create(
                account,
                "DEPOSIT",
                amount,
                before,
                after
        );

        log.info("Deposit successful. Account: {}, New Balance: {}",
                accountNumber,
                after);

        return transaction;
    }

    @Transactional
    public Transactions withdraw(String accountNumber, BigDecimal amount) {

        log.info("Withdrawal request received. Account: {}, Amount: {}", accountNumber, amount);

        validateAmount(amount);

        Accounts account = validateOwnership(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {

            log.warn("Withdrawal failed. Insufficient balance. Account: {}", accountNumber);

            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal before = account.getBalance();
        BigDecimal after = before.subtract(amount);

        updateBalance(account, after);

        Transactions transaction = transactionFactory.create(
                account,
                "WITHDRAW",
                amount,
                before,
                after
        );

        log.info("Withdrawal successful. Account: {}, Remaining Balance: {}",
                accountNumber,
                after);

        return transaction;
    }

    /**
     * Validate transaction amount.
     */
    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {

            log.warn("Invalid transaction amount: {}", amount);

            throw new InvalidTransactionException(
                    "Amount must be greater than zero");
        }
    }

    /**
     * Updates account balance.
     */
    private void updateBalance(Accounts account, BigDecimal newBalance) {

        account.setBalance(newBalance);
        accountRepository.save(account);

        log.debug("Balance updated for account {}", account.getAccountNumber());
    }


    private Accounts validateOwnership(String accountNumber) {

        Accounts account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {

                    log.warn("Account not found: {}", accountNumber);

                    return new ResourceNotFoundException("Account not found");
                });

        User currentUser = currentUserService.getCurrentUser();

        if (!account.getUser().getUserId().equals(currentUser.getUserId())) {

            log.warn(
                    "Unauthorized access attempt. User: {}, Account: {}",
                    currentUser.getEmail(),
                    accountNumber
            );

            throw new UnauthorizedAccessException(
                    "You do not have permission to access this account");
        }

        return account;
    }
}
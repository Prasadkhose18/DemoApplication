package com.demo.demo.service.impl;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import com.demo.demo.exception.InsufficientBalanceException;
import com.demo.demo.exception.InvalidTransactionException;
import com.demo.demo.exception.InvalidStatementRequestException;
import com.demo.demo.exception.ResourceNotFoundException;
import com.demo.demo.exception.UnauthorizedAccessException;
import com.demo.demo.repository.AccountRepository;
import com.demo.demo.security.services.CurrentUserService;
import com.demo.demo.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
public class ValidationServiceImpl implements ValidationService {

    private final AccountRepository accountRepository;
    private final CurrentUserService currentUserService;

    public ValidationServiceImpl(AccountRepository accountRepository,
                                 CurrentUserService currentUserService) {
        this.accountRepository = accountRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {

            log.warn("Invalid transaction amount: {}", amount);

            throw new InvalidTransactionException(
                    "Amount must be greater than zero");
        }
    }

    @Override
    public void validateTransferAccounts(String fromAccountNumber,
                                         String toAccountNumber) {

        if (fromAccountNumber.equals(toAccountNumber)) {

            log.warn("Transfer attempted to same account: {}", fromAccountNumber);

            throw new InvalidTransactionException(
                    "Cannot transfer to the same account");
        }
    }

    @Override
    public void validateSufficientBalance(Accounts account,
                                          BigDecimal amount) {

        if (account.getBalance().compareTo(amount) < 0) {

            log.warn(
                    "Insufficient balance. Account: {}, Available: {}, Requested: {}",
                    account.getAccountNumber(),
                    account.getBalance(),
                    amount
            );

            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }
    }

    @Override
    public Accounts validateOwnership(String accountNumber) {

        Accounts account = getAccount(accountNumber);

        // ADMIN can access everything
        if (currentUserService.isAdmin()) {

            log.debug(
                    "Admin {} accessing account {}",
                    currentUserService.getCurrentUserEmail(),
                    accountNumber
            );

            return account;
        }

        User currentUser = currentUserService.getCurrentUser();

        if (!account.getUser().getUserId().equals(currentUser.getUserId())) {

            log.warn(
                    "Unauthorized access. User: {}, Account: {}",
                    currentUser.getEmail(),
                    accountNumber
            );

            throw new UnauthorizedAccessException(
                    "You do not have permission to access this account");
        }

        log.debug(
                "Ownership verified. User: {}, Account: {}",
                currentUser.getEmail(),
                accountNumber
        );

        return account;
    }

    @Override
    public Accounts getAccount(String accountNumber) {

        log.debug("Fetching account {}", accountNumber);

        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {

                    log.warn("Account not found: {}", accountNumber);

                    return new ResourceNotFoundException(
                            "Account not found");
                });
    }

    @Override
    public Accounts getAccountForUpdate(String accountNumber) {

        log.debug("Fetching account {} with a write lock", accountNumber);

        return accountRepository.findWithLockByAccountNumber(accountNumber)
                .orElseThrow(() -> {

                    log.warn("Account was not found: {}", accountNumber);

                    return new ResourceNotFoundException("Account not found");
                });
    }

    @Override
    public void validateStatementRequest(String accountNumber,
                                         LocalDate fromDate,
                                         LocalDate toDate) {

        if (accountNumber == null || accountNumber.isBlank()) {
            log.warn("Invalid statement request: account number is missing");
            throw new InvalidStatementRequestException("Account number is required");
        }

        if (fromDate == null || toDate == null) {
            log.warn("Invalid statement request: date range is missing");
            throw new InvalidStatementRequestException(
                    "From date and to date are required");
        }

        LocalDate today = LocalDate.now();
        if (fromDate.isAfter(toDate)) {
            log.warn("Invalid statement request: from date {} is after to date {}",
                    fromDate, toDate);
            throw new InvalidStatementRequestException(
                    "From date cannot be after to date");
        }

        if (toDate.isAfter(today)) {
            log.warn("Invalid statement request: to date {} is in the future", toDate);
            throw new InvalidStatementRequestException(
                    "To date cannot be in the future");
        }

        if (fromDate.isBefore(today.minusMonths(3))) {
            log.warn("Invalid statement request: from date {} is older than three months",
                    fromDate);
            throw new InvalidStatementRequestException(
                    "From date cannot be older than three months");
        }
    }

    @Override
    public void validateAccountIsActive(Accounts account) {

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            log.warn("Account validation failed. Account: {}, Status: {}",
                    account.getAccountNumber(), account.getStatus());
            throw new InvalidStatementRequestException(
                    "Account is not active");
        }
    }

    @Override
    public void updateBalance(Accounts account,
                              BigDecimal newBalance) {

        account.setBalance(newBalance);

        accountRepository.save(account);

        log.debug(
                "Balance updated for account {}",
                account.getAccountNumber()
        );
    }
}

package com.demo.demo.service.impl;

import com.demo.demo.dto.BalanceResponseDTO;
import com.demo.demo.dto.TransferRequestDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.enums.TransactionType;
import com.demo.demo.exception.InsufficientBalanceException;
import com.demo.demo.factory.TransactionFactory;
import com.demo.demo.model.TransferResult;
import com.demo.demo.service.TransactionService;
import com.demo.demo.service.ValidationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final ValidationService validationService;
    private final TransactionFactory transactionFactory;

    public TransactionServiceImpl(ValidationService validationService,
                                  TransactionFactory transactionFactory) {
        this.validationService = validationService;
        this.transactionFactory = transactionFactory;
    }

    @Transactional
    public Transactions deposit(String accountNumber, BigDecimal amount) {

        log.info("Deposit request received. Account: {}, Amount: {}",
                accountNumber,
                amount);

        validationService.validateAmount(amount);

        Accounts account = validationService.validateOwnership(accountNumber);

        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(amount);

        validationService.updateBalance(account, after);

        Transactions transaction = createTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                before,
                after
        );

        log.info("Deposit successful. Reference: {}",
                transaction.getReferenceId());

        return transaction;
    }

    @Transactional
    public Transactions withdraw(String accountNumber, BigDecimal amount) {

        log.info("Withdrawal request received. Account: {}, Amount: {}",
                accountNumber,
                amount);

        validationService.validateAmount(amount);

        Accounts account = validationService.validateOwnership(accountNumber);

        validateSufficientBalance(account, amount);

        BigDecimal before = account.getBalance();
        BigDecimal after = before.subtract(amount);

        validationService.updateBalance(account, after);

        Transactions transaction = createTransaction(
                account,
                TransactionType.WITHDRAW,
                amount,
                before,
                after
        );

        log.info("Withdrawal successful. Reference: {}",
                transaction.getReferenceId());

        return transaction;
    }

    public BigDecimal checkBalance(String accountNumber) {

        log.info("Balance enquiry requested for account {}",
                accountNumber);

        Accounts account = validationService.validateOwnership(accountNumber);

        log.info("Balance enquiry successful. Account: {}",
                accountNumber);

        return account.getBalance();
    }

    @Transactional
    public TransferResult transfer(TransferRequestDTO request) {

        String fromAccountNumber = request.getFromAccountNumber();
        String toAccountNumber = request.getToAccountNumber();
        BigDecimal amount = request.getAmount();

        log.info(
                "Transfer initiated. From: {}, To: {}, Amount: {}",
                fromAccountNumber,
                toAccountNumber,
                amount
        );

        validationService.validateAmount(amount);

        Accounts sender =
                validationService.validateOwnership(fromAccountNumber);

        Accounts receiver =
                validationService.getAccount(toAccountNumber);

        validateSufficientBalance(sender, amount);

        BigDecimal senderBefore = sender.getBalance();
        BigDecimal senderAfter = senderBefore.subtract(amount);

        BigDecimal receiverBefore = receiver.getBalance();
        BigDecimal receiverAfter = receiverBefore.add(amount);

        validationService.updateBalance(sender, senderAfter);
        validationService.updateBalance(receiver, receiverAfter);

        Transactions debitTransaction = createTransaction(
                sender,
                TransactionType.TRANSFER_DEBIT,
                amount,
                senderBefore,
                senderAfter
        );

        Transactions creditTransaction = createTransaction(
                receiver,
                TransactionType.TRANSFER_CREDIT,
                amount,
                receiverBefore,
                receiverAfter
        );

        log.info(
                "Transfer completed successfully. From: {}, To: {}",
                fromAccountNumber,
                toAccountNumber
        );

        return TransferResult.builder()
                .debitTransaction(debitTransaction)
                .creditTransaction(creditTransaction)
                .build();
    }

    public BalanceResponseDTO getBalance(String accountNumber) {

        log.info("Balance enquiry received for account {}", accountNumber);

        Accounts account = validationService.validateOwnership(accountNumber);

        log.info("Balance fetched successfully for account {}", accountNumber);

        return BalanceResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    private Transactions createTransaction(
            Accounts account,
            TransactionType type,
            BigDecimal amount,
            BigDecimal before,
            BigDecimal after) {

        return transactionFactory.create(
                account,
                type,
                amount,
                before,
                after,
                null
        );
    }

    private void validateSufficientBalance(
            Accounts account,
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
}

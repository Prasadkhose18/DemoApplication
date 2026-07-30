package com.demo.demo.service.impl;

import com.demo.demo.audit.event.TransactionCompletedEvent;
import com.demo.demo.audit.producer.TransactionEventPublisher;
import com.demo.demo.dto.internal.TransactionFactoryRequest;
import com.demo.demo.dto.response.BalanceResponseDTO;
import com.demo.demo.dto.request.TransferRequestDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.enums.TransactionType;
import com.demo.demo.exception.InsufficientBalanceException;
import com.demo.demo.factory.TransactionFactory;
import com.demo.demo.model.TransferResult;
import com.demo.demo.repository.TransactionRepository;
import com.demo.demo.service.TransactionService;
import com.demo.demo.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final ValidationService validationService;
    private final TransactionFactory transactionFactory;
    private final TransactionRepository transactionRepository;
    private final TransactionEventPublisher transactionEventPublisher;

    public TransactionServiceImpl(ValidationService validationService,
                                  TransactionFactory transactionFactory,
                                  TransactionRepository transactionRepository,
                                  TransactionEventPublisher transactionEventPublisher) {
        this.validationService = validationService;
        this.transactionFactory = transactionFactory;
        this.transactionRepository = transactionRepository;
        this.transactionEventPublisher = transactionEventPublisher;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transactions deposit(String accountNumber, BigDecimal amount) {

        log.info("Deposit request received. Account: {}, Amount: {}",
                accountNumber,
                amount);

        validationService.validateAmount(amount);

        Accounts account = validationService.getAccountForUpdate(accountNumber);
        validationService.validateOwnership(accountNumber);

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

        publishCompletedEvent(transaction, null);

        log.info("Deposit successful. Reference: {}",
                transaction.getReferenceId());

        return transaction;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transactions withdraw(String accountNumber, BigDecimal amount) {

        log.info("Withdrawal request received. Account: {}, Amount: {}",
                accountNumber,
                amount);

        validationService.validateAmount(amount);

        Accounts account = validationService.getAccountForUpdate(accountNumber);
        validationService.validateOwnership(accountNumber);

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

        publishCompletedEvent(transaction, null);

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

    @Transactional(rollbackFor = Exception.class)
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
        validationService.validateTransferAccounts(
                fromAccountNumber,
                toAccountNumber
        );

        // Acquire locks in a consistent order. This prevents two opposing
        // transfers from deadlocking while ensuring balances cannot be read
        // or changed concurrently.
        boolean senderComesFirst =
                fromAccountNumber.compareTo(toAccountNumber) < 0;

        Accounts firstLockedAccount = validationService.getAccountForUpdate(
                senderComesFirst ? fromAccountNumber : toAccountNumber
        );
        Accounts secondLockedAccount = validationService.getAccountForUpdate(
                senderComesFirst ? toAccountNumber : fromAccountNumber
        );

        Accounts sender =
                senderComesFirst ? firstLockedAccount : secondLockedAccount;
        validationService.validateOwnership(fromAccountNumber);

        Accounts receiver =
                senderComesFirst ? secondLockedAccount : firstLockedAccount;

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

        publishCompletedEvent(debitTransaction, receiver.getAccountNumber());

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

        TransactionFactoryRequest request =
                TransactionFactoryRequest.builder()
                        .account(account)
                        .transactionType(type)
                        .amount(amount)
                        .balanceBefore(before)
                        .balanceAfter(after)
                        .referenceId(null)
                        .build();

        return transactionRepository.save(transactionFactory.create(request));
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

    private void publishCompletedEvent(Transactions transaction,
                                       String counterpartyAccountNumber) {

        transactionEventPublisher.publishAfterCommit(
                new TransactionCompletedEvent(
                        UUID.randomUUID(),
                        transaction.getReferenceId(),
                        transaction.getTransactionType(),
                        transaction.getAccount().getAccountNumber(),
                        counterpartyAccountNumber,
                        transaction.getAmount(),
                        transaction.getBalanceBefore(),
                        transaction.getBalanceAfter(),
                        transaction.getTransactionTime()
                )
        );
    }
}

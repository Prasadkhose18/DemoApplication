package com.demo.demo.service.impl;

import com.demo.demo.event.TransactionCompletedEvent;
import com.demo.demo.event.TransactionEventPublisher;
import com.demo.demo.dto.internal.TransactionFactoryRequest;
import com.demo.demo.dto.response.BalanceResponseDTO;
import com.demo.demo.dto.request.TransferRequestDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.entity.User;
import com.demo.demo.enums.TransactionType;
import com.demo.demo.exception.InsufficientBalanceException;
import com.demo.demo.factory.TransactionFactory;
import com.demo.demo.model.TransferResult;
import com.demo.demo.repository.TransactionRepository;
import com.demo.demo.service.TransactionService;
import com.demo.demo.service.ValidationService;
import com.demo.demo.security.services.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final ValidationService validationService;
    private final TransactionFactory transactionFactory;
    private final TransactionRepository transactionRepository;
    private final TransactionEventPublisher transactionEventPublisher;
    private final CurrentUserService currentUserService;


    public TransactionServiceImpl(
            ValidationService validationService,
            TransactionFactory transactionFactory,
            TransactionRepository transactionRepository,
            TransactionEventPublisher transactionEventPublisher,
            CurrentUserService currentUserService) {

        this.validationService = validationService;
        this.transactionFactory = transactionFactory;
        this.transactionRepository = transactionRepository;
        this.transactionEventPublisher = transactionEventPublisher;
        this.currentUserService = currentUserService;
    }


    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "balance", key = "#accountNumber")
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

        publishCompletedEvent(transaction);

        log.info("Deposit successful. Reference: {}",
                transaction.getReferenceId());

        return transaction;
    }



    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "balance", key = "#accountNumber")
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

        publishCompletedEvent(transaction);

        log.info("Withdrawal successful. Reference: {}",
                transaction.getReferenceId());

        return transaction;
    }



    public BigDecimal checkBalance(String accountNumber) {

        log.info("Balance enquiry requested for account {}",
                accountNumber);

        Accounts account =
                validationService.validateOwnership(accountNumber);

        log.info("Balance enquiry successful. Account: {}",
                accountNumber);

        return account.getBalance();
    }



    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "balance", allEntries = true)
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


        boolean senderComesFirst =
                fromAccountNumber.compareTo(toAccountNumber) < 0;


        Accounts firstLockedAccount =
                validationService.getAccountForUpdate(
                        senderComesFirst ?
                                fromAccountNumber :
                                toAccountNumber
                );


        Accounts secondLockedAccount =
                validationService.getAccountForUpdate(
                        senderComesFirst ?
                                toAccountNumber :
                                fromAccountNumber
                );


        Accounts sender =
                senderComesFirst ?
                        firstLockedAccount :
                        secondLockedAccount;


        validationService.validateOwnership(fromAccountNumber);


        Accounts receiver =
                senderComesFirst ?
                        secondLockedAccount :
                        firstLockedAccount;


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

        publishCompletedEvent(debitTransaction);


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





    @Cacheable(value = "balance", key = "#accountNumber")
    public BalanceResponseDTO getBalance(String accountNumber) {

        log.info("Balance enquiry received for account {}", accountNumber);

        Accounts account =
                validationService.validateOwnership(accountNumber);

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

        return transactionRepository.save(
                transactionFactory.create(request)
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




    private void publishCompletedEvent(Transactions transaction) {

        User currentUser =
                currentUserService.getCurrentUser();


        transactionEventPublisher.publish(
                new TransactionCompletedEvent(
                        transaction.getTransactionId(),
                        transaction.getReferenceId(),
                        transaction.getAccount().getAccountNumber(),
                        transaction.getAccount().getAccountType(),
                        currentUser.getName(),
                        currentUser.getEmail(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getBalanceAfter(),
                        transaction.getTransactionTime()
                )
        );
    }


}

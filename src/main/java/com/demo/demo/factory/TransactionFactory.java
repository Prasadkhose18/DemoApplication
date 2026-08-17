package com.demo.demo.factory;

import com.demo.demo.dto.internal.TransactionFactoryRequest;
import com.demo.demo.entity.Transactions;
import com.demo.demo.enums.ReferenceType;
import com.demo.demo.util.ReferenceIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TransactionFactory {

    private final ReferenceIdGenerator referenceIdGenerator;

    public TransactionFactory(ReferenceIdGenerator referenceIdGenerator) {
        this.referenceIdGenerator = referenceIdGenerator;
    }

    public Transactions create(TransactionFactoryRequest request) {

        log.debug(
                "Creating {} transaction for account {}",
                request.transactionType(),
                request.account().getAccountNumber()
        );

        Transactions transaction = new Transactions();

        transaction.setAccount(request.account());
        transaction.setTransactionType(request.transactionType());
        transaction.setAmount(request.amount());
        transaction.setBalanceBefore(request.balanceBefore());
        transaction.setBalanceAfter(request.balanceAfter());
        String referenceId = resolveReferenceId(request.referenceId());
        transaction.setReferenceId(referenceId);
        transaction.setTransactionTime(LocalDateTime.now());

        log.info(
                "Transaction object created. Reference: {}",
                transaction.getReferenceId()
        );

        return transaction;
    }

    private String resolveReferenceId(String referenceId) {

        if (referenceId != null && !referenceId.isBlank()) {
            return referenceId;
        }

        return referenceIdGenerator.generate(ReferenceType.TRANSACTION);
    }
}

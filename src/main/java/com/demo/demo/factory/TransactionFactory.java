package com.demo.demo.factory;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class TransactionFactory {

    public Transactions create(
            Accounts account,
            TransactionType transactionType,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String referenceId) {

        log.debug(
                "Creating {} transaction for account {}",
                transactionType,
                account.getAccountNumber()
        );

        Transactions transaction = new Transactions();

        transaction.setAccount(account);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setReferenceId(resolveReferenceId(referenceId));
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

        return "TXN-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
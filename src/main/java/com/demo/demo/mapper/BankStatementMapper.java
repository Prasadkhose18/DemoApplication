package com.demo.demo.mapper;

import com.demo.demo.dto.response.BankStatementResponseDTO;
import com.demo.demo.dto.response.TransactionStatementDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BankStatementMapper {

    public BankStatementResponseDTO toResponseDTO(Accounts account,
                                                   LocalDate fromDate,
                                                   LocalDate toDate,
                                                   List<Transactions> transactions) {

        BankStatementResponseDTO response = new BankStatementResponseDTO();
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setHolderName(account.getUser().getName());
        response.setFromDate(fromDate);
        response.setToDate(toDate);
        response.setGeneratedAt(LocalDateTime.now());
        response.setTotalTransactions(transactions.size());
        response.setTransactions(transactions.stream()
                .map(this::toTransactionStatementDTO)
                .toList());

        return response;
    }

    private TransactionStatementDTO toTransactionStatementDTO(
            Transactions transaction) {

        TransactionStatementDTO dto = new TransactionStatementDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setReferenceId(transaction.getReferenceId());
        dto.setTransactionType(transaction.getTransactionType().name());
        dto.setDescription(getDescription(transaction));
        dto.setAmount(transaction.getAmount());
        dto.setBalanceBefore(transaction.getBalanceBefore());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        dto.setTransactionTime(transaction.getTransactionTime());

        return dto;
    }

    private String getDescription(Transactions transaction) {
        return switch (transaction.getTransactionType()) {
            case DEPOSIT -> "Deposit";
            case WITHDRAW -> "Withdrawal";
            case TRANSFER_DEBIT -> "Transfer sent";
            case TRANSFER_CREDIT -> "Transfer received";
            case REVERSAL -> "Transaction reversal";
        };
    }
}

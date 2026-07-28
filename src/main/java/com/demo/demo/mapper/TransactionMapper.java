package com.demo.demo.mapper;


import com.demo.demo.dto.response.TransactionResponseDTO;
import com.demo.demo.entity.Transactions;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toResponseDTO(Transactions transaction) {

        TransactionResponseDTO dto = new TransactionResponseDTO();

        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        dto.setReferenceId(transaction.getReferenceId());
        dto.setTransactionType(transaction.getTransactionType().name());
        dto.setAmount(transaction.getAmount());
        dto.setBalanceBefore(transaction.getBalanceBefore());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        dto.setTransactionTime(transaction.getTransactionTime());

        return dto;
    }
}

package com.demo.demo.mapper;

import com.demo.demo.dto.response.TransferResponseDTO;
import com.demo.demo.model.TransferResult;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

    public TransferResponseDTO toResponseDTO(
            TransferResult result) {

        return TransferResponseDTO.builder()

                .referenceId(
                        result.getDebitTransaction()
                                .getReferenceId())

                .fromAccountNumber(
                        result.getDebitTransaction()
                                .getAccount()
                                .getAccountNumber())

                .toAccountNumber(
                        result.getCreditTransaction()
                                .getAccount()
                                .getAccountNumber())

                .amount(
                        result.getDebitTransaction()
                                .getAmount())

                .senderBalance(
                        result.getDebitTransaction()
                                .getBalanceAfter())

                .receiverBalance(
                        result.getCreditTransaction()
                                .getBalanceAfter())

                .transactionTime(
                        result.getDebitTransaction()
                                .getTransactionTime())

                .build();
    }
}
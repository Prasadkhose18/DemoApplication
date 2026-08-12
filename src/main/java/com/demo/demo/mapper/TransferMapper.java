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
                        result.debitTransaction()
                                .getReferenceId())

                .fromAccountNumber(
                        result.debitTransaction()
                                .getAccount()
                                .getAccountNumber())

                .toAccountNumber(
                        result.creditTransaction()
                                .getAccount()
                                .getAccountNumber())

                .amount(
                        result.debitTransaction()
                                .getAmount())

                .senderBalance(
                        result.debitTransaction()
                                .getBalanceAfter())


                .transactionTime(
                        result.debitTransaction()
                                .getTransactionTime())

                .build();
    }
}

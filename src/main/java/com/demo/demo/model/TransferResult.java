package com.demo.demo.model;

import com.demo.demo.entity.Transactions;
import lombok.Builder;

@Builder
public record TransferResult(
        Transactions debitTransaction,
        Transactions creditTransaction) {
}

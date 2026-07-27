package com.demo.demo.model;

import com.demo.demo.entity.Transactions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResult {

    private Transactions debitTransaction;
    private Transactions creditTransaction;
}
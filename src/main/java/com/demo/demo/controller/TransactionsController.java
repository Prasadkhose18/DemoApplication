package com.demo.demo.controller;

import com.demo.demo.dto.ApiResponse;
import com.demo.demo.dto.TransactionRequestDTO;
import com.demo.demo.dto.TransactionResponseDTO;
import com.demo.demo.entity.Transactions;
import com.demo.demo.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionsController {

    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ===================== DEPOSIT =====================
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> deposit(
            @Valid @RequestBody TransactionRequestDTO request,
            HttpServletRequest httpRequest) {


        Transactions txn = transactionService.deposit(
                request.getAccountNumber(),
                request.getAmount()
        );
        System.out.println("Received Credentials");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "Deposit successful",
                        mapToResponse(txn),
                        httpRequest.getRequestURI()
                ));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> withdraw(
            @Valid @RequestBody TransactionRequestDTO request,
            HttpServletRequest httpRequest) {

        Transactions txn = transactionService.withdraw(
                request.getAccountNumber(),
                request.getAmount()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "Withdrawal successful",
                        mapToResponse(txn),
                        httpRequest.getRequestURI()
                ));
    }



    private TransactionResponseDTO mapToResponse(Transactions txn) {
        TransactionResponseDTO res = new TransactionResponseDTO();
        res.setAccountNumber(txn.getAccount().getAccountNumber());
        res.setReferenceId(txn.getReferenceId());
        res.setTransactionType(txn.getTransactionType());
        res.setAmount(txn.getAmount());
        res.setBalanceBefore(txn.getBalanceBefore());
        res.setBalanceAfter(txn.getBalanceAfter());
        res.setTransactionTime(txn.getTransactionTime());
        return res;
    }
}

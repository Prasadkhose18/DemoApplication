package com.demo.demo.controller;

import com.demo.demo.dto.ApiResponse;
import com.demo.demo.dto.BalanceResponseDTO;
import com.demo.demo.dto.TransactionRequestDTO;
import com.demo.demo.dto.TransactionResponseDTO;
import com.demo.demo.dto.TransferRequestDTO;
import com.demo.demo.dto.TransferResponseDTO;
import com.demo.demo.entity.Transactions;
import com.demo.demo.mapper.TransactionMapper;
import com.demo.demo.mapper.TransferMapper;
import com.demo.demo.model.TransferResult;
import com.demo.demo.service.ITransactionService;
import com.demo.demo.util.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    private final ITransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final TransferMapper transferMapper;

    public TransactionsController(ITransactionService transactionService,
                                  TransactionMapper transactionMapper,
                                  TransferMapper transferMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.transferMapper = transferMapper;
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> deposit(
            @Valid @RequestBody TransactionRequestDTO request,
            HttpServletRequest httpRequest) {

        Transactions transaction = transactionService.deposit(
                request.getAccountNumber(),
                request.getAmount());

        return ApiResponseUtil.created(
                transactionMapper.toResponseDTO(transaction),
                "Deposit successful",
                httpRequest.getRequestURI());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> withdraw(
            @Valid @RequestBody TransactionRequestDTO request,
            HttpServletRequest httpRequest) {

        Transactions transaction = transactionService.withdraw(
                request.getAccountNumber(),
                request.getAmount());

        return ApiResponseUtil.created(
                transactionMapper.toResponseDTO(transaction),
                "Withdrawal successful",
                httpRequest.getRequestURI());
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransferResponseDTO>> transfer(
            @Valid @RequestBody TransferRequestDTO request,
            HttpServletRequest httpRequest) {

        TransferResult result = transactionService.transfer(request);

        return ApiResponseUtil.created(
                transferMapper.toResponseDTO(result),
                "Transfer successful",
                httpRequest.getRequestURI());
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<ApiResponse<BalanceResponseDTO>> getBalance(
            @PathVariable String accountNumber,
            HttpServletRequest httpRequest) {

        BalanceResponseDTO response =
                transactionService.getBalance(accountNumber);

        return ApiResponseUtil.success(
                response,
                "Balance fetched successfully",
                httpRequest.getRequestURI());
    }
}
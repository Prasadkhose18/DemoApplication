package com.demo.demo.controller;

import com.demo.demo.dto.AccountRequestDTO;
import com.demo.demo.dto.AccountResponseDTO;
import com.demo.demo.dto.ApiResponse;
import com.demo.demo.entity.Accounts;
import com.demo.demo.mapper.AccountMapper;
import com.demo.demo.service.AccountsService;
import com.demo.demo.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountsService accountsService;
    private final AccountMapper accountMapper;
    private final ResponseBuilder responseBuilder;

    public AccountController(AccountsService accountsService,
                             AccountMapper accountMapper,
                             ResponseBuilder responseBuilder) {
        this.accountsService = accountsService;
        this.accountMapper = accountMapper;
        this.responseBuilder = responseBuilder;
    }

    @PostMapping("/createAccount")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(
            @Valid @RequestBody AccountRequestDTO requestDTO,
            HttpServletRequest request) {

        log.info("Create account request received. Type: {}", requestDTO.getAccountType());

        Accounts account = accountsService.createAccount(requestDTO.getAccountType());

        log.info("Account created successfully. Account Number: {}", account.getAccountNumber());

        return responseBuilder.created(
                "Account created successfully",
                accountMapper.responseDTO(account),
                request.getRequestURI()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getMyAccounts(
            HttpServletRequest request) {

        log.info("Fetching current user's accounts.");

        List<AccountResponseDTO> response = accountsService.getMyAccounts()
                .stream()
                .map(accountMapper::responseDTO)
                .toList();

        log.info("Returned {} account(s).", response.size());

        return responseBuilder.ok(
                "Accounts fetched successfully",
                response,
                request.getRequestURI()
        );
    }
}
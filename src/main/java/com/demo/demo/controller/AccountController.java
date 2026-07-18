package com.demo.demo.controller;

import com.demo.demo.dto.AccountRequestDTO;
import com.demo.demo.dto.AccountResponseDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.service.AccountsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountsService accountsService;
    public AccountController(AccountsService accountsService){
        this.accountsService= accountsService;
    }

    // -- Create--

    @PostMapping("/createAccount")
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountRequestDTO requestDTO
            ){

        Accounts account = accountsService.createAccount(requestDTO.getAccountType());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToResponse(account));
    }


    @GetMapping("/me")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts(){
        List<AccountResponseDTO> response =
                accountsService.getMyAccounts()
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private AccountResponseDTO mapToResponse(Accounts account) {

        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setAccountId(account.getAccountId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        return dto;
    }


}

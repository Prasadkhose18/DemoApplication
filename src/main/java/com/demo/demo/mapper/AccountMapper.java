package com.demo.demo.mapper;


import com.demo.demo.dto.AccountResponseDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountResponseDTO responseDTO(Accounts account){

        AccountResponseDTO dto = new AccountResponseDTO();

        dto.setAccountId(account.getAccountId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());

        return dto;
    }
}

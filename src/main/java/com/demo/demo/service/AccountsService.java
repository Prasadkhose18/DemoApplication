package com.demo.demo.service;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import com.demo.demo.repository.AccountRepository;
import com.demo.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountsService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountsService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    // Create Account---

    @Transactional
    public Accounts createAccount(String accountType){
        String email = "prasad@example.com";

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User Not found"));

        Accounts account = new Accounts();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType.toUpperCase());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setUser(user);

        return accountRepository.save(account);
    }

    public List<Accounts> getMyAccounts(){
        String email = "prasad@example.com";
        return accountRepository.findByUserEmail(email);
    }

    private String generateAccountNumber(){
        return "ACC-"+ UUID.randomUUID()
                .toString()
                .replace("-","")
                .substring(0,10)
                .toUpperCase();
    }
}

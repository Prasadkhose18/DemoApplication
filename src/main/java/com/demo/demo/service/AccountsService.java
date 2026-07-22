package com.demo.demo.service;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import com.demo.demo.factory.AccountFactory;
import com.demo.demo.repository.AccountRepository;
import com.demo.demo.repository.UserRepository;
import com.demo.demo.security.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AccountsService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountFactory accountFactory;

    public AccountsService(AccountRepository accountRepository,
                           UserRepository userRepository,
                           AccountFactory accountFactory) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountFactory = accountFactory;

    }

    // Create Account---

    @Transactional
    public Accounts createAccount(String accountType){
        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User Not found"));

        Accounts account = accountFactory.createAccount(user, accountType);

        return accountRepository.save(account);
    }

    public List<Accounts> getMyAccounts(){
        String email = SecurityUtil.getCurrentUserEmail();
        return accountRepository.findByUserEmail(email);
    }

}

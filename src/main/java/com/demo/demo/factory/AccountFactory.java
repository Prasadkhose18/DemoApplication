package com.demo.demo.factory;


import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class AccountFactory {
    public Accounts createAccount(User user, String accountType) {

        Accounts account = new Accounts();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType.toUpperCase());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setUser(user);

        return account;
    }


private String generateAccountNumber(){
        return "ACC-"+ UUID.randomUUID()
                .toString()
                .replace("-","")
                .substring(0,10)
                .toUpperCase();
    }
}



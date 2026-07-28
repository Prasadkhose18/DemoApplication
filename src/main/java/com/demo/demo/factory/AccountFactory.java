package com.demo.demo.factory;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import com.demo.demo.enums.ReferenceType;
import com.demo.demo.util.ReferenceIdGenerator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountFactory {

    private final ReferenceIdGenerator referenceIdGenerator;

    public AccountFactory(ReferenceIdGenerator referenceIdGenerator) {
        this.referenceIdGenerator = referenceIdGenerator;
    }

    public Accounts createAccount(User user, String accountType) {

        Accounts account = new Accounts();

        account.setAccountNumber(
                referenceIdGenerator.generate(ReferenceType.ACCOUNT)
        );
        account.setAccountType(accountType.toUpperCase());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setUser(user);

        return account;
    }
}
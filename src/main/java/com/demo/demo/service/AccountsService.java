package com.demo.demo.service;

import com.demo.demo.entity.Accounts;
import java.util.List;

public interface AccountsService {

    Accounts createAccount(String accountType);

    List<Accounts> getMyAccounts();
}

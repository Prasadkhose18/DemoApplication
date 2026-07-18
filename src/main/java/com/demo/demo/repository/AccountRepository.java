package com.demo.demo.repository;

import com.demo.demo.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {

    Optional<Accounts> findByAccountNumber(String accountNumber);
    List<Accounts> findByUserUserId(Long userId);
    List<Accounts> findByUserEmail(String email);

}

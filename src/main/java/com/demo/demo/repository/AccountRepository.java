package com.demo.demo.repository;

import com.demo.demo.entity.Accounts;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {

    Optional<Accounts> findByAccountNumber(String accountNumber);

    /**
     * Acquires a database write lock until the surrounding transaction commits
     * or rolls back. Use this for every balance-changing operation.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Accounts> findWithLockByAccountNumber(String accountNumber);

    List<Accounts> findByUserEmail(String email);

    Optional<Accounts> findByAccountNumberAndUserUserId(
            String accountNumber,
            Long userId
    );

}

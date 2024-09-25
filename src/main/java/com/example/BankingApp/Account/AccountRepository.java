package com.example.BankingApp.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {




    Optional<Account> findByIdNumber(String idNumber);

    @Query("SELECT a.accountNumber FROM Account a WHERE a.accountNumber LIKE ?1% ORDER BY a.accountNumber DESC LIMIT 1")
    String findLatestAccountNumber(String yearMonth);

    Optional<Account> findByAccountNumber(String accountNumber);

}

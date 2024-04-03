package com.example.BankingApp.Repository;

import com.example.BankingApp.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}

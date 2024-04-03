package com.example.BankingApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private String accountHolderName;
    private Long accountNumber;
    private double balance;

    public AccountDto(Long id, String accountHolderName, double balance) {

    }
}

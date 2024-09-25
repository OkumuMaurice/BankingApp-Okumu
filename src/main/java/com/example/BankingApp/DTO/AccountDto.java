package com.example.BankingApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AccountDto {

    private String accountHolderName;
    private double balance;
    private String idNumber;
    private String phoneNumber;
    private String accountType;

}

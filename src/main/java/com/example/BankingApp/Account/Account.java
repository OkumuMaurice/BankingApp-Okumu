package com.example.BankingApp.Account;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountHolderName;
    private String idNumber;
    private String phoneNumber;
    private String accountNumber;
    private String accountType;  // individual, joint, organisation, business
    private Double balance;



}
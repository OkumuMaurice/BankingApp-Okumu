package com.example.BankingApp.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "accounts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(name = "account_holder_name")
    private String accountHolderName;
    private Long accountNumber;
    private double balance;

    public Account(Long id, String accountHolderName, double balance) {

    }
}

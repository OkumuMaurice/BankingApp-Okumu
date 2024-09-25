package com.example.BankingApp.Account;

import com.example.BankingApp.DTO.AccountDto;
import com.example.BankingApp.ExceptionHandler.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Create new account
    @PostMapping("create_newAccount")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto) {
        Account newAccount = accountService.createAccount(accountDto);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    // Get Account
    @GetMapping("get_account/{idNumber}")
    public ResponseEntity<?> getAccountByIdNumber(@PathVariable String idNumber) {
        Optional<Account> account = accountService.getAccountByIdNumber(idNumber);
        if (account.isPresent()) {
            return new ResponseEntity<>(account.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found for the given ID number: " + idNumber, HttpStatus.NOT_FOUND);
        }
    }

    // Get all accounts
    @GetMapping("All_accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Deposit API
    @PutMapping("{idNumber}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable String idNumber, @RequestBody Map<String, Object> request) {
        Double amount = (Double) request.get("amount");
        if (amount == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Account account = accountService.depositByIdNumber(idNumber, amount);
        return ResponseEntity.ok(account);
    }

    // Withdraw API
    @PutMapping("{idNumber}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String idNumber, @RequestBody Map<String, Object> request) {
        try {
            Double amount = (Double) request.get("amount");
            if (amount == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount is required");
            }
            Account account = accountService.withdrawByIdNumber(idNumber, amount);
            return ResponseEntity.ok(account);
        } catch (InsufficientBalanceException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // Update account details
    @PutMapping("update/{idNumber}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable String idNumber,
            @RequestBody AccountDto accountDto) {
        Account updatedAccount = accountService.updateAccountByIdNumber(idNumber, accountDto);
        return ResponseEntity.ok(updatedAccount);
    }

    // Transferring money
    @PostMapping("transfer")
    public ResponseEntity<String> transferMoney(
            @RequestParam String senderAccountNumber,
            @RequestParam String receiverAccountNumber,
            @RequestParam double amount) {
        String responseMessage = accountService.transferMoney(senderAccountNumber, receiverAccountNumber, amount);
        return ResponseEntity.ok(responseMessage);
    }

    // Delete account
    @DeleteMapping("delete/{idNumber}")
    public ResponseEntity<String> deleteAccount(@PathVariable String idNumber) {
        boolean isDeleted = accountService.deleteAccountByIdNumber(idNumber);
        if (isDeleted) {
            return new ResponseEntity<>("Account with ID number " + idNumber + " deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found for the given ID number: " + idNumber, HttpStatus.NOT_FOUND);
        }
    }
}

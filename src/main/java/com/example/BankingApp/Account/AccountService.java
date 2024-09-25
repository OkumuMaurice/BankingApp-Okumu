package com.example.BankingApp.Account;

import com.example.BankingApp.DTO.AccountDto;
import com.example.BankingApp.ExceptionHandler.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private String generateAccountNumber() {
        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear());
        String month = String.format("%02d", currentDate.getMonthValue());
        String day = String.format("%02d", currentDate.getDayOfMonth());

        // Combine year, month, and day for uniqueness
        String yearMonthDay = year + month + day;

        // Retrieve the latest account number for the specific day
        String latestAccountNumber = accountRepository.findLatestAccountNumber(yearMonthDay);

        int lastSequence = 0;
        if (latestAccountNumber != null && latestAccountNumber.length() > 8) {
            String lastSequenceStr = latestAccountNumber.substring(8);
            lastSequence = Integer.parseInt(lastSequenceStr);
        }

        // Increment the sequence for the next account number
        int nextSequence = lastSequence + 1;
        String nextSequenceStr = String.format("%03d", nextSequence);

        // Return the new account number
        return yearMonthDay + nextSequenceStr;
    }

    public Account createAccount(AccountDto accountDto) {
        Optional<Account> existingAccount = accountRepository.findByIdNumber(accountDto.getIdNumber());

        if (existingAccount.isPresent()) {
            throw new RuntimeException("Account already exists for this ID number: " + accountDto.getIdNumber());
        }
        Account account = new Account();
        account.setAccountHolderName(accountDto.getAccountHolderName());
        account.setIdNumber(accountDto.getIdNumber());
        account.setPhoneNumber(accountDto.getPhoneNumber());

        String newAccountNumber = generateAccountNumber();
        account.setAccountNumber(newAccountNumber);
        account.setAccountType(accountDto.getAccountType());
        account.setBalance(0.0);

        return accountRepository.save(account);
    }

    // Get all accounts
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Get account by idNumber
    public Optional<Account> getAccountByIdNumber(String idNumber) {
        return accountRepository.findByIdNumber(idNumber);
    }

    // Deposit method using idNumber
    public Account depositByIdNumber(String idNumber, double amount) {
        Account account = accountRepository
                .findByIdNumber(idNumber)
                .orElseThrow(() -> new RuntimeException("Account does not exist for this ID number"));

        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        return account;
    }

    // Withdraw method using idNumber
    public Account withdrawByIdNumber(String idNumber, double amount) {
        Account account = accountRepository
                .findByIdNumber(idNumber)
                .orElseThrow(() -> new RuntimeException("Account does not exist for this ID number"));

        double balance = account.getBalance();
        if (balance < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        account.setBalance(balance - amount);
        accountRepository.save(account);

        return account;
    }

    // Update account details by idNumber
    public Account updateAccountByIdNumber(String idNumber, AccountDto accountDto) {

        Account existingAccount = accountRepository.findByIdNumber(idNumber)
                .orElseThrow(() -> new RuntimeException("Account not found for ID number: " + idNumber));

        if (accountDto.getAccountHolderName() != null) {
            existingAccount.setAccountHolderName(accountDto.getAccountHolderName());
        }
        if (accountDto.getIdNumber() != null) {
            existingAccount.setIdNumber(accountDto.getIdNumber());
        }
        if (accountDto.getPhoneNumber() != null){
            existingAccount.setPhoneNumber(accountDto.getPhoneNumber());
        }

        return accountRepository.save(existingAccount);
    }

    // Transfer money
    public String transferMoney(String senderAccountNumber, String receiverAccountNumber, double amount) {
        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found for account number: " + senderAccountNumber));

        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found for account number: " + receiverAccountNumber));

        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in sender's account");
        }

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a, M/d/yyyy");
        String transferTime = now.format(formatter);

        // confirmation message
        return "Confirmed, KSH " + amount + " has been successfully transferred from " + senderAccount.getAccountNumber()  + ", " +
                senderAccount.getAccountHolderName() + " to "  +  receiverAccount.getAccountNumber() + ", " + receiverAccount.getAccountHolderName() +
                " at " + transferTime + ".";
    }

    // Delete account by idNumber
    public boolean deleteAccountByIdNumber(String idNumber) {
        Optional<Account> account = accountRepository.findByIdNumber(idNumber);
        if (account.isPresent()) {
            accountRepository.delete(account.get());
            return true;
        }
        return false;
    }


}

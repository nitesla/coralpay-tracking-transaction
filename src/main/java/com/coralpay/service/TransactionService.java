package com.coralpay.service;


import com.coralpay.dto.request.TransactionRequest;
import com.coralpay.dto.request.TransactionSummary;
import com.coralpay.model.Transaction;
import com.coralpay.model.User;
import com.coralpay.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CurrencyConversionService currencyConversionService;

    public void createTransaction(TransactionRequest transactionRequest) {
        // Implement transaction creation logic and store equivalent USD value
        // User object representing the currently logged-in user
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Convert transaction amount to USD
        double usdValue = currencyConversionService.convertToUSD(transactionRequest.getAmount(), transactionRequest.getCurrency(), "USD");

        // Create a new transaction entity
        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setCurrency(transactionRequest.getCurrency());
        transaction.setUsdValue(usdValue);
        transaction.setTransactionDate(LocalDateTime.now());

        // Set other transaction details as needed

        // Save the transaction to the database
        transactionRepository.save(transaction);
    }

    public TransactionSummary getTransactionSummary() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Get transactions for the week (you may need to customize this based on your date criteria)
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        List<Transaction> transactions = transactionRepository.findByTransactionDateAfterAndUserId(oneWeekAgo, user.getId());

        // Calculate summary values
        double currentUsdValue = calculateTotalUsdValue(transactions);
        double initialUsdValue = transactions.stream().mapToDouble(Transaction::getUsdValue).sum();
        double usdGainLoss = currentUsdValue - initialUsdValue;
        double ngnGainLoss = 0.0;
        if (usdGainLoss != 0.0) {
            ngnGainLoss = currencyConversionService.convertToNGN(usdGainLoss, "USD", "NGN");
        }


        // Create and return TransactionSummary
        TransactionSummary summary = new TransactionSummary();
        summary.setCurrentUsdValue(currentUsdValue);
        summary.setInitialUsdValue(initialUsdValue);
        summary.setNgnGainLoss(ngnGainLoss);
        summary.setTransactions(transactions);

        return summary;
    }

    private double calculateTotalUsdValue(List<Transaction> transactions) {
        // Sum up the USD values of all transactions
        return transactions.stream().mapToDouble(Transaction::getUsdValue).sum();
    }
}

package com.coralpay.dto.request;


import com.coralpay.model.Transaction;
import lombok.Data;

import java.util.List;
@Data
public class TransactionSummary {
    private double currentUsdValue; // USD value at the time of summary request
    private double initialUsdValue; // USD value at the time of transaction creation
    private double ngnGainLoss; // NGN gain/loss in value as compared to USD
    private List<Transaction> transactions; // List of transactions for the week
}
package com.coralpay.controller;


import com.coralpay.dto.request.TransactionRequest;
import com.coralpay.dto.request.TransactionSummary;
import com.coralpay.service.TransactionService;
import com.coralpay.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        transactionService.createTransaction(transactionRequest);
        return ResponseEntity.ok("Transaction created successfully");
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionSummary> getTransactionSummary() {
        TransactionSummary summary = transactionService.getTransactionSummary();
        return ResponseEntity.ok(summary);
    }
}

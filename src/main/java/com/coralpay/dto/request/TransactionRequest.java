package com.coralpay.dto.request;

import lombok.Data;

@Data
public class TransactionRequest {
    private double amount;
    private String currency;
}
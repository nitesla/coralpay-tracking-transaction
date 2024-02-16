package com.coralpay.dto.response;


import com.coralpay.model.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionSummary {

    private double currentUsdValue;
    private double initialUsdValue;
    private double ngnGainLoss;
    private List<Transaction> transactions;

}

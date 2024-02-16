package com.coralpay.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyConversionResponse {


    @JsonProperty("base")
    private String base;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("result")
    private ConversionResult result;

    @JsonProperty("ms")
    private int milliseconds;

}

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
public class ConversionResult {

    @JsonProperty("NGN")
    private double ngn;

    @JsonProperty("rate")
    private double rate;

    @JsonProperty("USD")
    private double usd;




}

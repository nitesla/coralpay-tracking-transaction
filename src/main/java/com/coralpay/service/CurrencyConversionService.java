package com.coralpay.service;


import com.coralpay.dto.response.CurrencyConversionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyConversionService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${currency.api.url}")
    private String currencyApiUrl;

    @Value("${currency.api.accessKey}")
    private String accessKey;

    public double convertToUSD(double amount, String from, String to) {
        String apiUrl = String.format("%s?api_key=%s&from=%s&to=%s&amount=%s", currencyApiUrl, accessKey, from, to, amount);

        // Make a request to the currency conversion API
        RestTemplate restTemplate = new RestTemplate();
        CurrencyConversionResponse response = restTemplate.getForObject(apiUrl, CurrencyConversionResponse.class);
        return response != null ? response.getResult().getUsd() : 0;

    }

    public double convertToNGN(double amount, String from, String to) {
        String apiUrl = String.format("%s?api_key=%s&from=%s&to=%s&amount=%s", currencyApiUrl, accessKey, from, to, amount);

        // Make a request to the currency conversion API
        RestTemplate restTemplate = new RestTemplate();
        CurrencyConversionResponse response = restTemplate.getForObject(apiUrl, CurrencyConversionResponse.class);
        return response != null ? response.getResult().getNgn() : 0;

    }
}

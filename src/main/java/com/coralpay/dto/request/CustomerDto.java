package com.coralpay.dto.request;


import lombok.Data;


@Data
public class CustomerDto {

    private Long id;
    private String rcNumber;
    private String address;
    private String companyName;
    private String phone;
    private String email;
    private String registrationToken;
    private String registrationTokenExpiration;


}

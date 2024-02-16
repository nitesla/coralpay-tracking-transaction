package com.coralpay.dto.request;


import lombok.Data;


@Data
public class CustomerSignUpDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String companyName;
}

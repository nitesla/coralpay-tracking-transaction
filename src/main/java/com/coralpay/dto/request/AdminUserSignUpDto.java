package com.coralpay.dto.request;

import lombok.Data;

@Data
public class AdminUserSignUpDto {

    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private String password;
}

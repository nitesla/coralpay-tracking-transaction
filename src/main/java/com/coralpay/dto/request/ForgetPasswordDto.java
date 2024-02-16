package com.coralpay.dto.request;

import lombok.Data;

@Data
public class ForgetPasswordDto {

    private String email;
    private String phone;
}

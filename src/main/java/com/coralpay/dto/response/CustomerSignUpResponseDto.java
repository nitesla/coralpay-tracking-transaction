package com.coralpay.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSignUpResponseDto {
    private Long id;
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String companyName;
    private String username;
}

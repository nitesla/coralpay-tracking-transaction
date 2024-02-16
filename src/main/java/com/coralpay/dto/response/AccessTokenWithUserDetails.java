package com.coralpay.dto.response;

import com.coralpay.enums.UserCategory;
import com.coralpay.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenWithUserDetails implements Serializable{


    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;


    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("middleName")
    private String middleName;


    @JsonProperty("lastLogin")
    private LocalDateTime lastLogin;

    @JsonProperty("tokenExpiry")
    private long tokenExpiry;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("isEmailVerified")
    private String isEmailVerified ;

    @JsonProperty("photo")
    private String photo;

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("userCategory")
    private String userCategory;






    public AccessTokenWithUserDetails(String token, User user, long tokenExpiry, String customerId, UserCategory userCategory) {
        this.accessToken = token;
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.middleName= user.getMiddleName();
        this.lastLogin = user.getLastLogin();
        this.tokenExpiry = tokenExpiry;
        this.userId=user.getId();
        this.customerId= customerId;
        this.userCategory= String.valueOf(userCategory);

    }

}

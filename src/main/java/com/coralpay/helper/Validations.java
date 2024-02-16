package com.coralpay.helper;

import com.coralpay.dto.request.CustomerDto;
import com.coralpay.dto.request.CustomerSignUpDto;
import com.coralpay.exceptions.BadRequestException;
import com.coralpay.repository.CustomerRepository;
import com.coralpay.repository.UserRepository;
import com.coralpay.utils.CustomResponseCode;
import com.coralpay.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@SuppressWarnings("All")
@Slf4j
@Service
public class Validations {

   
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;




    public Validations(UserRepository userRepository) {
       
        this.userRepository = userRepository;
    }

    public String generateReferenceNumber(int numOfDigits) {
        if (numOfDigits < 1) {
            throw new IllegalArgumentException(numOfDigits + ": Number must be equal or greater than 1");
        }
        long random = (long) Math.floor(Math.random() * 9 * (long) Math.pow(10, numOfDigits - 1)) + (long) Math.pow(10, numOfDigits - 1);
        return Long.toString(random);
    }

    public String generateCode(String code) {
        String encodedString = Base64.getEncoder().encodeToString(code.getBytes());
        return encodedString;
    }

    public void validateCustomer(CustomerSignUpDto customer){
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "First name cannot be empty");
        if (customer.getFirstName().length() < 2 || customer.getFirstName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid first name  length");
        if (customer.getLastName() == null || customer.getLastName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Last name cannot be empty");
        if (customer.getLastName().length() < 2 || customer.getLastName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid last name  length");

        if (customer.getEmail() == null || customer.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "email cannot be empty");
        if (!Utility.validEmail(customer.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        if (customer.getPhone() == null || customer.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone number cannot be empty");
        if (customer.getPhone().length() < 8 || customer.getPhone().length() > 14)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid phone number  length");
        if (!Utility.isNumeric(customer.getPhone()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for phone number ");
        if (customer.getCompanyName() == null || customer.getCompanyName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

    }



    public void validateCustomerUpdate(CustomerDto customerPropertiesDto) {
        if (customerPropertiesDto.getCompanyName() == null || customerPropertiesDto.getCompanyName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

        if (customerPropertiesDto.getAddress() == null || customerPropertiesDto.getAddress().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Address cannot be empty");

        if (customerPropertiesDto.getPhone() == null || customerPropertiesDto.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone cannot be empty");
        if (customerPropertiesDto.getEmail() == null || customerPropertiesDto.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Email cannot be empty");
    }



}



package com.coralpay.controller;

import com.coralpay.dto.request.ChangePasswordDto;
import com.coralpay.dto.request.CustomerDto;
import com.coralpay.dto.request.CustomerSignUpDto;
import com.coralpay.dto.request.EnableDisableDto;
import com.coralpay.dto.response.CustomerActivationResponse;
import com.coralpay.dto.response.CustomerResponseDto;
import com.coralpay.dto.response.CustomerSignUpResponseDto;
import com.coralpay.dto.response.Response;
import com.coralpay.model.Customer;
import com.coralpay.service.CustomerService;
import com.coralpay.utils.Constants;
import com.coralpay.utils.CustomResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"customer")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }



    @PostMapping("/signup")
    public ResponseEntity<Response> customerSignUp(@Validated @RequestBody CustomerSignUpDto request, HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        CustomerSignUpResponseDto response = service.customerSignUp(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.CREATED;
        return new ResponseEntity<>(resp, httpCode);
    }

    @PutMapping("/passwordactivation")
    public ResponseEntity<Response> CustomerPasswordActivation(@Valid @RequestBody ChangePasswordDto request){
        HttpStatus httpCode ;
        Response resp = new Response();
        CustomerActivationResponse response = service.customerPasswordActivation(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Password changed successfully");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

    @PostMapping("")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> createCustomerProperties(@Validated @RequestBody CustomerDto request){
        HttpStatus httpCode ;
        Response resp = new Response();
        CustomerResponseDto response = service.createCustomerProperties(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.CREATED;
        return new ResponseEntity<>(resp, httpCode);
    }


    @PutMapping("")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> updateCustomerProperties(@Validated @RequestBody  CustomerDto request,HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        CustomerResponseDto response = service.updateCustomerProperties(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Update Successful");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }



    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> getCustomerProperty(@PathVariable Long id){
        HttpStatus httpCode ;
        Response resp = new Response();
        CustomerResponseDto response = service.findCustomerAsset(id);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    @PutMapping("/enabledisable")
    public ResponseEntity<Response> enableDisEnable(@Validated @RequestBody EnableDisableDto request, HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        service.enableDisEnable(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    @GetMapping("/list")
    public ResponseEntity<Response> getAll(@RequestParam(value = "isActive")Boolean isActive){
        HttpStatus httpCode ;
        Response resp = new Response();
        List<Customer> response = service.getAll(isActive);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

}

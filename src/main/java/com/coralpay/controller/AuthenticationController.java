package com.coralpay.controller;

import com.coralpay.config.AuthenticationWithToken;
import com.coralpay.dto.request.LoginRequest;
import com.coralpay.dto.response.AccessTokenWithUserDetails;
import com.coralpay.dto.response.Response;
import com.coralpay.enums.UserCategory;
import com.coralpay.exceptions.LockedException;
import com.coralpay.exceptions.UnauthorizedException;
import com.coralpay.model.Customer;
import com.coralpay.model.User;
import com.coralpay.repository.CustomerRepository;
import com.coralpay.service.TokenService;
import com.coralpay.service.UserService;
import com.coralpay.utils.Constants;
import com.coralpay.utils.CustomResponseCode;
import com.coralpay.utils.LoggerUtil;
import com.coralpay.utils.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Slf4j
@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"authenticate")
public class AuthenticationController {

    @Value("${login.attempts}")
    private int loginAttempts;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CustomerRepository customerRepository;

    private final UserService userService;


    public AuthenticationController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) throws JsonProcessingException {

        log.info(":::::::::: login Request %s:::::::::::::" + loginRequest.getUsername());
        String loginStatus;
        String ipAddress = Utility.getClientIp(request);
        User user = userService.loginUser(loginRequest);
        if (user != null) {
            if (user.isLoginStatus()) {
                //FIRST TIME LOGIN
                if (user.getPasswordChangedOn() == null) {
                    Response resp = new Response();
                    resp.setCode(CustomResponseCode.CHANGE_P_REQUIRED);
                    resp.setDescription("Change password Required, account has not been activated");
                    return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);//202
                }
                if (user.getIsActive()==false) {
                    Response resp = new Response();
                    resp.setCode(CustomResponseCode.FAILED);
                    resp.setDescription("User Account Deactivated, please contact Administrator");
                    return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (user.getLoginAttempts() >= loginAttempts || user.getLockedDate() != null) {
                    // lock account after x failed attempts or locked date is not null
                    userService.lockLogin(user.getId());
                    throw new LockedException(CustomResponseCode.LOCKED_EXCEPTION, "This account has been locked, Kindly contact support");
                }
//                userService.validateGeneratedPassword(user.getId());
            } else {
                //update login failed count and failed login date
                loginStatus = "failed";
                userService.updateFailedLogin(user.getId());
                throw new UnauthorizedException(CustomResponseCode.UNAUTHORIZED, "Invalid Login details.");
            }
        } else {
            //NO NEED TO update login failed count and failed login date SINCE IT DOES NOT EXIST
            throw new UnauthorizedException(CustomResponseCode.UNAUTHORIZED, "Login details does not exist");
        }

        AuthenticationWithToken authWithToken = new AuthenticationWithToken(user, null);
        String newToken = "Bearer" +" "+this.tokenService.generateNewToken();
        authWithToken.setToken(newToken);
        tokenService.store(newToken, authWithToken);
        SecurityContextHolder.getContext().setAuthentication(authWithToken);
        userService.updateLogin(user.getId());

        String customerId = "";
        if (user.getUserCategory().equals(UserCategory.CUSTOMER)) {
            Customer customer = customerRepository.findByUserId(user.getId());
            if(customer !=null){
                customerId = String.valueOf(customer.getId());
            }
        }

        AccessTokenWithUserDetails details = new AccessTokenWithUserDetails(newToken, user, userService.getSessionExpiry(), customerId, user.getUserCategory());
        return new ResponseEntity<>(details, HttpStatus.OK);
    }





    @PostMapping("/logout")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean logout() {
        try {
            AuthenticationWithToken auth = (AuthenticationWithToken) SecurityContextHolder.getContext().getAuthentication();
            return tokenService.remove(auth.getToken());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            LoggerUtil.logError(log, ex);
        }
        return false;
    }



}

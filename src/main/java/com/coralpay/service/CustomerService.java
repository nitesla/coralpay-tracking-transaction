package com.coralpay.service;

import com.coralpay.dto.request.ChangePasswordDto;
import com.coralpay.dto.request.CustomerDto;
import com.coralpay.dto.request.CustomerSignUpDto;
import com.coralpay.dto.request.EnableDisableDto;
import com.coralpay.dto.response.CustomerActivationResponse;
import com.coralpay.dto.response.CustomerResponseDto;
import com.coralpay.dto.response.CustomerSignUpResponseDto;
import com.coralpay.enums.UserCategory;
import com.coralpay.exceptions.BadRequestException;
import com.coralpay.exceptions.ConflictException;
import com.coralpay.exceptions.NotFoundException;
import com.coralpay.helper.Validations;
import com.coralpay.model.Customer;
import com.coralpay.model.PreviousPasswords;
import com.coralpay.model.User;
import com.coralpay.notification.requestDto.NotificationRequestDto;
import com.coralpay.notification.requestDto.RecipientRequest;
import com.coralpay.repository.CustomerRepository;
import com.coralpay.repository.PreviousPasswordRepository;
import com.coralpay.repository.UserRepository;
import com.coralpay.utils.CustomResponseCode;
import com.coralpay.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
@Slf4j
@Service
public class CustomerService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    //@Autowired
    //private WhatsAppService whatsAppService;
    private CustomerRepository repository;
    private UserRepository userRepository;
    private PreviousPasswordRepository previousPasswordRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;
    private NotificationService notificationService;


    public CustomerService(CustomerRepository repository, UserRepository userRepository,
                           PreviousPasswordRepository previousPasswordRepository, ModelMapper mapper,
                           ObjectMapper objectMapper, Validations validations, NotificationService notificationService){
        this.repository = repository;
        this.userRepository = userRepository;
        this.previousPasswordRepository = previousPasswordRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
        this.notificationService = notificationService;
    }

    public CustomerSignUpResponseDto customerSignUp(CustomerSignUpDto request, HttpServletRequest request1) {
        validations.validateCustomer(request);
        User user = mapper.map(request,User.class);

        User exist = userRepository.findByEmailOrPhone(request.getEmail(),request.getPhone());
        if(exist !=null && exist.getPasswordChangedOn()== null){

            Customer customerExist = repository.findByUserId(exist.getId());
            if(customerExist !=null){
                CustomerSignUpResponseDto customerSignUpResponseDto= CustomerSignUpResponseDto.builder()
                  .id(exist.getId())
                  .email(exist.getEmail())
                  .firstName(exist.getFirstName())
                  .lastName(exist.getLastName())
                  .phone(exist.getPhone())
                  .username(exist.getUsername())
                  .customerId(customerExist.getId())
                  .build();
          return customerSignUpResponseDto;
            }else {
                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " Customer id does not exist");
            }

        }else if(exist !=null && exist.getPasswordChangedOn() !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Customer user already exist");
        }
        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setUserCategory(UserCategory.CUSTOMER);
        user.setUsername(request.getEmail());
        user.setLoginAttempts(0);
        user.setResetToken(Utility.registrationCode("HHmmss"));
        user.setResetTokenExpirationDate(Utility.tokenExpiration());
        user.setCreatedBy(0l);
        user.setIsActive(false);
        user = userRepository.save(user);
        log.debug("Create new customer - {}"+ new Gson().toJson(user));

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

        Customer saveCustomer = new Customer();
        saveCustomer.setUserId(user.getId());
        saveCustomer.setIsActive(false);
        saveCustomer.setCreatedBy(user.getId());
        saveCustomer.setCompanyName(request.getCompanyName());

        Customer customerResponse= repository.save(saveCustomer);
        log.debug("Create new Customer  - {}"+ new Gson().toJson(saveCustomer));


        CustomerSignUpResponseDto response = CustomerSignUpResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .companyName(customerResponse.getCompanyName())
                .customerId(customerResponse.getId())
                .build();

        // --------  sending token  -----------

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        User emailRecipient = userRepository.getOne(user.getId());
        notificationRequestDto.setMessage("Coralpay Account Activation Otp: " + " " + user.getResetToken());
        List<RecipientRequest> recipient = new ArrayList<>();
        recipient.add(RecipientRequest.builder()
                .email(emailRecipient.getEmail())
                .build());
        notificationRequestDto.setRecipient(recipient);
        notificationRequestDto.setMail(emailRecipient.getEmail());
        notificationService.emailNotificationRequest(notificationRequestDto);

//        SmsRequest smsRequest = SmsRequest.builder()
//                .message("Coralpay Account Activation Otp: " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        notificationService.smsNotificationRequest(smsRequest);

//        WhatsAppRequest whatsAppRequest = WhatsAppRequest.builder()
//                .message("Coralpay Account Activation Otp: " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        whatsAppService.whatsAppNotification(whatsAppRequest);

//        VoiceOtpRequest voiceOtpRequest = VoiceOtpRequest.builder()
//                .message("Coralpay Account Activation Otp: " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        notificationService.voiceOtp(voiceOtpRequest);

        return response;
    }

    public CustomerActivationResponse customerPasswordActivation(ChangePasswordDto request) {

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        mapper.map(request, user);

        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChangedOn(LocalDateTime.now());
        user = userRepository.save(user);

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

        Customer customer = repository.findByUserId(user.getId());

        CustomerActivationResponse response = CustomerActivationResponse.builder()
                .userId(user.getId())
                .customerId(customer.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

        return response;
    }

    public CustomerResponseDto createCustomerProperties(CustomerDto request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Customer customerProperties = mapper.map(request,Customer.class);
        Customer exist = repository.findCustomerById(request.getId());
        if(exist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Customer properties already exist");
        }
        customerProperties.setCreatedBy(userCurrent.getId());
        customerProperties.setIsActive(true);
        customerProperties = repository.save(customerProperties);
        log.debug("Create new customer properties - {}"+ new Gson().toJson(customerProperties));
        return mapper.map(customerProperties, CustomerResponseDto.class);
    }


    public CustomerResponseDto updateCustomerProperties(CustomerDto request, HttpServletRequest request1) {
        validations.validateCustomerUpdate(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Customer customerProperties = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested customer properties Id does not exist!"));
        mapper.map(request, customerProperties);
        customerProperties.setUpdatedBy(userCurrent.getId());
        repository.save(customerProperties);
        log.debug("customer asset record updated - {}"+ new Gson().toJson(customerProperties));

        return mapper.map(customerProperties, CustomerResponseDto.class);
    }


    public CustomerResponseDto findCustomerAsset(Long id){
        Customer customerProperties  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested customer properties Id does not exist!"));
        return mapper.map(customerProperties,CustomerResponseDto.class);
    }


    public Page<Customer> findAll(String name, PageRequest pageRequest ){
        Page<Customer> customerProperties = repository.findCustomersProperties(name,pageRequest);
        if(customerProperties == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }

        return customerProperties;

    }



    public void enableDisEnable (EnableDisableDto request, HttpServletRequest request1){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Customer customerProperties = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested customer properties Id does not exist!"));
        customerProperties.setIsActive(request.getIsActive());
        customerProperties.setUpdatedBy(userCurrent.getId());
        repository.save(customerProperties);

    }


    public List<Customer> getAll(Boolean isActive){
        List<Customer> customerProperties = repository.findByIsActive(isActive);
        return customerProperties;

    }
}

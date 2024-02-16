package com.coralpay.exceptions;

public class FailedRequestException extends AbstractException {

    public FailedRequestException(String code, String message) {
        super(code, message);

    }
}

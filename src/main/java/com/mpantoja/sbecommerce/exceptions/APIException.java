package com.mpantoja.sbecommerce.exceptions;

public class APIException extends RuntimeException{

    private static Long serialversionUID;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}

package com.api.countryinfo.exception;

public class InvalidCountryNameException  extends RuntimeException{
    public InvalidCountryNameException(String message) {
        super(message);
    }
}

package com.assignment.spring.exceptions;

public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException() {
        super();
    }
    public CityNotFoundException(String message) {
        super(message);
    }
}

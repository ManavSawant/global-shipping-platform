package com.MNS.global_shipping_platform.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String message) {
        super("Customer already exists: " + message);
    }
}

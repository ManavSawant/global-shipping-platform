package com.MNS.global_shipping_platform.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super("Customer not found: " + message);
    }
}

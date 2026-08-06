package com.MNS.global_shipping_platform.customer;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super("Customer not found: " + message);
    }
}

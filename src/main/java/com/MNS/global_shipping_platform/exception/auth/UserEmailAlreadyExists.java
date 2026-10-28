package com.MNS.global_shipping_platform.exception.auth;

public class UserEmailAlreadyExists extends RuntimeException {

    public UserEmailAlreadyExists(String message) {
        super(message);
    }
}

package com.MNS.global_shipping_platform.exception;

public class SailingNumberAlreadyExists extends RuntimeException {

    public SailingNumberAlreadyExists(String message) {
        super("sailing number already exists: " + message);
    }
}

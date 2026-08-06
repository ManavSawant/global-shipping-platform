package com.MNS.global_shipping_platform.exception.port;

public class PortNotFoundException extends RuntimeException {
    public PortNotFoundException(String message) {
        super(message);
    }
}

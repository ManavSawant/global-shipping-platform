package com.MNS.global_shipping_platform.exception.vessel;

public class VesselAlreadyExistsException extends RuntimeException {

    public VesselAlreadyExistsException(String message) {
        super(message);
    }
}

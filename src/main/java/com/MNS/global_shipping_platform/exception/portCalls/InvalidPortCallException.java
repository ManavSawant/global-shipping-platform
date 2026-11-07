package com.MNS.global_shipping_platform.exception.portCalls;

import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;

public class InvalidPortCallException extends RuntimeException {

    public InvalidPortCallException(String message) {
        super(message);
    }
}

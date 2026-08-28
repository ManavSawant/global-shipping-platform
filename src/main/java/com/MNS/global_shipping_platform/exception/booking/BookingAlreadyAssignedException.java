package com.MNS.global_shipping_platform.exception.booking;

public class BookingAlreadyAssignedException extends RuntimeException {
    public BookingAlreadyAssignedException(String message) {
        super(message);
    }
}

package com.MNS.global_shipping_platform.booking.service;

import com.MNS.global_shipping_platform.booking.BookingStatus;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingStatusException;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusValidator {

    public void validateTransition(
            BookingStatus currentStatus,
            BookingStatus newStatus) {

        boolean valid = switch (currentStatus){

            case PENDING ->
                newStatus == BookingStatus.APPROVED
                        || newStatus == BookingStatus.REJECTED
                        || newStatus == BookingStatus.CANCELLED;

            case APPROVED ->
                newStatus == BookingStatus.CANCELLED;

            case IN_TRANSIT ->
                newStatus == BookingStatus.DELIVERED;

            case REJECTED, CANCELLED, DELIVERED -> false;
        };

        if (!valid) {
            throw new InvalidBookingStatusException("Invalid booking status transition: "
                    + currentStatus + "->"
                    + newStatus
            );
        }
    }

}

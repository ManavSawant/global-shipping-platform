package com.MNS.global_shipping_platform.booking.dto;

import com.MNS.global_shipping_platform.booking.BookingStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID bookingId,
        UUID sailingId,
        String sailingNumber,
        String customerName,
        String originPortName,
        String destinationPortName,
        String cargoType,
        Integer cargoWeight,
        Integer numberOfContainers,
        BookingStatus bookingStatus,
        LocalDateTime createdDate
) {}

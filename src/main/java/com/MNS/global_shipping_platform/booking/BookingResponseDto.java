package com.MNS.global_shipping_platform.booking;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponseDto(
        UUID bookingId,
        String customerName,
        String originPortName,
        String destinationPortName,
        String cargoType,
        Integer cargoWeight,
        Integer numberOfContainers,
        BookingStatus bookingStatus,
        LocalDateTime createdDate
) {}

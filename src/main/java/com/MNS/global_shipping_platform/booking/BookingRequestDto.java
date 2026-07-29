package com.MNS.global_shipping_platform.booking;

import java.util.UUID;

public record BookingRequestDto(
        UUID customerId,
        UUID originPortId,
        UUID destinationPortId,
        String cargoType,
        Integer cargoWeight,
        Integer numberOfContainers
) {}
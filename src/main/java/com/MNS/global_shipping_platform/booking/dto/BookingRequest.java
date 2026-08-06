package com.MNS.global_shipping_platform.booking.dto;

import java.util.UUID;

public record BookingRequest(
        UUID customerId,
        UUID originPortId,
        UUID destinationPortId,
        String cargoType,
        Integer cargoWeight,
        Integer numberOfContainers
) {}
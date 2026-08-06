package com.MNS.global_shipping_platform.sailing.dto;

import java.time.LocalDateTime;

public record UpdateSailingRequest(
        String sailingNumber,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
}

package com.MNS.global_shipping_platform.sailing.dto;

import java.time.LocalDateTime;


public record SailingRequest(String sailingNumber,
                             LocalDateTime departureTime,
                             LocalDateTime arrivalTime
) {
}

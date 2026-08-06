package com.MNS.global_shipping_platform.sailing.dto;

import com.MNS.global_shipping_platform.sailing.SailingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SailingResponse(UUID sailingId,
                              String sailingNumber,
                              UUID vesselId,
                              String vesselName,
                              LocalDateTime departureTime,
                              LocalDateTime arrivalTime,
                              SailingStatus status
) {
}

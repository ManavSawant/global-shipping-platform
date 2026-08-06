package com.MNS.global_shipping_platform.sailing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PortCallRequest(UUID sailingId,
                              UUID portId,
                              Integer sequenceNumber,
                              LocalDateTime departureTime,
                              LocalDateTime arrivalTime) {
}

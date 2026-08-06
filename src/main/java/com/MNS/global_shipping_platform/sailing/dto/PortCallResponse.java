package com.MNS.global_shipping_platform.sailing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PortCallResponse(UUID id,
                               UUID sailingId,
                               UUID portId,
                               String portName,
                               String locode,
                               Integer SequenceNumber,
                               LocalDateTime departureTime,
                               LocalDateTime arrivalTime) {
}

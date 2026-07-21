package com.MNS.global_shipping_platform.sailing;

import com.MNS.global_shipping_platform.vessel.Vessel;
import com.MNS.global_shipping_platform.vessel.VesselStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SailingResponse(UUID id,
                              String sailingNumber,
                              UUID vesselId,
                              String vesselName,
                              LocalDateTime departureTime,
                              LocalDateTime arrivalTime,
                              SailingStatus status) {
}

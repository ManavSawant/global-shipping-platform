package com.MNS.global_shipping_platform.sailing;

import com.MNS.global_shipping_platform.vessel.Vessel;
import com.MNS.global_shipping_platform.vessel.VesselStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public record SailingRequest(String sailingNumber,
                             @Nullable UUID vesselId,
                             LocalDateTime departureTime,
                             LocalDateTime arrivalTime,
                             SailingStatus status) {

}

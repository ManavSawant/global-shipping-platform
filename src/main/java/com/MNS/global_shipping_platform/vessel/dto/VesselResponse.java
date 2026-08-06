package com.MNS.global_shipping_platform.vessel.dto;

import com.MNS.global_shipping_platform.vessel.VesselStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VesselResponse(UUID id,
                             String imoNumber,
                             String vesselName,
                             Integer capacityTeu,
                             Integer builtYear,
                             VesselStatus status,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
}

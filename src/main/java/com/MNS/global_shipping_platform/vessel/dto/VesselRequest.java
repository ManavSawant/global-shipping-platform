package com.MNS.global_shipping_platform.vessel.dto;

public record VesselRequest(String imoNumber,
                            String vesselName,
                            Integer capacityTeu,
                            Integer builtYear
) {
}

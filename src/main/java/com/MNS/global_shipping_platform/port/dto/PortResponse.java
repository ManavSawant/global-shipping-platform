package com.MNS.global_shipping_platform.port.dto;

import com.MNS.global_shipping_platform.port.PortStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PortResponse(
        UUID portId,
        String locode,
        String portName,
        String city,
        String country,
        PortStatus status,
        BigDecimal latitude,
        BigDecimal longitude
) {
}

package com.MNS.global_shipping_platform.port.dto;

import com.MNS.global_shipping_platform.port.PortStatus;

import java.math.BigDecimal;

public record PortRequest(String locode,
                          String portName,
                          String city,
                          String country,
                          BigDecimal latitude,
                          BigDecimal longitude) {
}

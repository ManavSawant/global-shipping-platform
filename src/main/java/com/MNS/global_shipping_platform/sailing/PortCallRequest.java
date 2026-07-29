package com.MNS.global_shipping_platform.sailing;

import java.time.LocalDateTime;
import java.util.UUID;

public record PortCallRequest(UUID sailing_id,
                              UUID port_id,
                              Integer sequence_number,
                              LocalDateTime departure_date,
                              LocalDateTime arrival_date) {
}

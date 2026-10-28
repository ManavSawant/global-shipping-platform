package com.MNS.global_shipping_platform.sailing.service;

import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.sailing.SailingStatus;
import org.springframework.stereotype.Component;

@Component
public class SailingStatusValidator {

    public void validateTransition(
            SailingStatus currentStatus,
            SailingStatus newStatus) {

        boolean valid = switch (currentStatus) {

            case PLANNED ->
                    newStatus == SailingStatus.SCHEDULED
                            || newStatus == SailingStatus.CANCELLED;

            case SCHEDULED ->
                    newStatus == SailingStatus.IN_PROGRESS
                            || newStatus == SailingStatus.CANCELLED;

            case IN_PROGRESS ->
                    newStatus == SailingStatus.COMPLETED;

            case COMPLETED, CANCELLED ->
                    false;
        };

        if (!valid) {
            throw new InvalidSailingScheduleException(
                    "Invalid sailing status transition: "
                            + currentStatus + " -> " + newStatus
            );
        }
    }
}
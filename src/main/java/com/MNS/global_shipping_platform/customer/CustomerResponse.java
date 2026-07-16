package com.MNS.global_shipping_platform.customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(UUID id,
                               String companyName,
                               String contactPersonFirstName,
                               String contactPersonLastName,
                               String customerEmail,
                               String phoneNumber,
                               CustomerType customerType,
                               LocalDateTime createdAt) {
}

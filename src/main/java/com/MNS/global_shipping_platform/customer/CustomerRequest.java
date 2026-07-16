package com.MNS.global_shipping_platform.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CustomerRequest(UUID id,
                              String companyName,
                              String contactPersonFirstName,
                              String contactPersonLastName,
                              String phoneNumber,
                              @NotBlank @Email String customerEmail,
                              CustomerType customerType) {
}

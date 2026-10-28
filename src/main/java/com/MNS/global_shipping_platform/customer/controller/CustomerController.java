package com.MNS.global_shipping_platform.customer.controller;

import com.MNS.global_shipping_platform.customer.dto.CustomerRequest;
import com.MNS.global_shipping_platform.customer.dto.CustomerResponse;
import com.MNS.global_shipping_platform.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable UUID id,
            Authentication authentication) {

        CustomerResponse response =
                customerService.findByCustomerId(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

        List<CustomerResponse> response =
                customerService.getAllCustomers();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest customerRequest,
            Authentication authentication) {

        CustomerResponse response =
                customerService.updateCustomer(
                        id,
                        customerRequest,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable UUID id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> createMyCustomerProfile(
            @Valid @RequestBody CustomerRequest customerRequest,
            Authentication authentication) {

        CustomerResponse response =
                customerService.createMyCustomerProfile(
                        customerRequest,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}

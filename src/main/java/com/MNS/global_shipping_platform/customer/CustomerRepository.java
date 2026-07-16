package com.MNS.global_shipping_platform.customer;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, UUID> {

    public Optional<Customer> findByCustomerEmail(String customerEmail);

    public boolean existsByCustomerEmail(String customerEmail);

    public Optional<Customer> findByCustomerId(UUID customerId);

    public List<Customer> getAllCustomers();


}

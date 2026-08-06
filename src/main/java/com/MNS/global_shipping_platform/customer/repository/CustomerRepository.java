package com.MNS.global_shipping_platform.customer.repository;

import com.MNS.global_shipping_platform.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, UUID> {

    public Optional<Customer> findByCustomerEmail(String customerEmail);

    public boolean existsByCustomerEmail(String customerEmail);

}

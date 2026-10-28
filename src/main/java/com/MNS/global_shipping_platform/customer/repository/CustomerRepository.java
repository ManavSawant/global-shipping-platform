package com.MNS.global_shipping_platform.customer.repository;

import com.MNS.global_shipping_platform.customer.entity.Customer;
import com.MNS.global_shipping_platform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByCustomerEmail(String customerEmail);

    Optional<Customer> findByUser(User user);

    Optional<Customer> findByUserEmail(String email);
}

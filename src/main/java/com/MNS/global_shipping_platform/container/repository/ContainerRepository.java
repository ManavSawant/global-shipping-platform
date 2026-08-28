package com.MNS.global_shipping_platform.container.repository;

import com.MNS.global_shipping_platform.container.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContainerRepository extends JpaRepository<Container, UUID> {

    boolean existsByContainerNumber(String containerNumber);

    List<Container> findByBookingId(UUID bookingId);

}

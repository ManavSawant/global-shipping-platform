package com.MNS.global_shipping_platform.booking.repository;

import com.MNS.global_shipping_platform.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findBySailingId(UUID sailingId);

}

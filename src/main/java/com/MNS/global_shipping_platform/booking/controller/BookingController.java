package com.MNS.global_shipping_platform.booking.controller;

import com.MNS.global_shipping_platform.booking.dto.BookingRequest;
import com.MNS.global_shipping_platform.booking.dto.BookingResponse;
import com.MNS.global_shipping_platform.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody BookingRequest bookingRequest) {
        return bookingService.createBooking(bookingRequest);
    }

    @GetMapping("/{id}")
    public BookingResponse getBookingById(@PathVariable UUID id) {
        return bookingService.getBookingsByBookingId(id);
    }
}

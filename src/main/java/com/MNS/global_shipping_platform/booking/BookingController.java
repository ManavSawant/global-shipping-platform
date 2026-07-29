package com.MNS.global_shipping_platform.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.createBooking(bookingRequestDto);
    }

    @GetMapping("/{id}")
    public BookingResponseDto getBookingById(@PathVariable UUID id) {
        return bookingService.getBookingsByBookingId(id);
    }
}

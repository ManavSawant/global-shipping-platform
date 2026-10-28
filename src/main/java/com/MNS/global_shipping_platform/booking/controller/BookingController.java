package com.MNS.global_shipping_platform.booking.controller;

import com.MNS.global_shipping_platform.booking.dto.BookingRequest;
import com.MNS.global_shipping_platform.booking.dto.BookingResponse;
import com.MNS.global_shipping_platform.booking.dto.BookingUpdateRequest;
import com.MNS.global_shipping_platform.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public BookingResponse createBooking(
            @RequestBody BookingRequest bookingRequest,
            Authentication authentication) {

        return bookingService.createBooking(
                bookingRequest,
                authentication.getName()
        );
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMIN')")
    @GetMapping("/{id}")
    public BookingResponse getBookingById(
            @PathVariable UUID id,
            Authentication authentication) {

        return bookingService.getBookingsById(
                id,
                authentication.getName()
        );
    }

    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @GetMapping
    public List<BookingResponse> getAllBookings() {

        return bookingService.getAllBookings();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{id}")
    public BookingResponse updateBooking(
            @PathVariable UUID id,
            @RequestBody BookingUpdateRequest updateRequest,
            Authentication authentication) {

        return bookingService.updateBooking(
                id,
                updateRequest,
                authentication.getName()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBookingById(
            @PathVariable UUID id) {

        bookingService.deleteBooking(id);
    }

    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @PatchMapping("/{id}/approve")
    public BookingResponse approveBooking(
            @PathVariable UUID id) {

        return bookingService.approveBooking(id);
    }

    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @PatchMapping("/{id}/reject")
    public BookingResponse rejectBooking(
            @PathVariable UUID id) {

        return bookingService.rejectBooking(id);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{id}/cancel")
    public BookingResponse cancelBooking(
            @PathVariable UUID id) {

        return bookingService.cancelBooking(id);
    }

    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @PatchMapping("/{bookingId}/assign-sailing/{sailingId}")
    public BookingResponse assignSailing(
            @PathVariable UUID bookingId,
            @PathVariable UUID sailingId) {

        return bookingService.assignSailingToBooking(
                bookingId,
                sailingId
        );
    }
}


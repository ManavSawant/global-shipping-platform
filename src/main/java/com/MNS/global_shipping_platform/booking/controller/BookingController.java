package com.MNS.global_shipping_platform.booking.controller;

import com.MNS.global_shipping_platform.booking.dto.BookingRequest;
import com.MNS.global_shipping_platform.booking.dto.BookingResponse;
import com.MNS.global_shipping_platform.booking.dto.BookingUpdateRequest;
import com.MNS.global_shipping_platform.booking.service.BookingService;
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
    public BookingResponse createBooking(@RequestBody BookingRequest bookingRequest) {
        return bookingService.createBooking(bookingRequest);
    }

    @GetMapping("/{id}")
    public BookingResponse getBookingById(@PathVariable UUID id) {
        return bookingService.getBookingsById(id);
    }

    @GetMapping
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PatchMapping("/{id}")
    public BookingResponse updateBooking(@PathVariable UUID id, @RequestBody BookingUpdateRequest updateRequest) {
        return bookingService.updateBooking(id,updateRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteBookingById(@PathVariable UUID id) {
        bookingService.deleteBooking(id);
    }

    @PatchMapping("/{id}/approve")
    public BookingResponse approveBooking(@PathVariable UUID id) {
        return bookingService.approveBooking(id);
    }

    @PatchMapping("/{id}/reject")
    public BookingResponse rejectBooking(@PathVariable UUID id) {
        return bookingService.rejectBooking(id);
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable UUID id) {
        return bookingService.cancelBooking(id);
    }

    @PatchMapping("/{bookingId}/assign-sailing/{sailingId}")
    public BookingResponse assignSailing(@PathVariable UUID bookingId, @PathVariable UUID sailingId) {
        return bookingService.assignSailingToBooking(bookingId, sailingId);
    }

}

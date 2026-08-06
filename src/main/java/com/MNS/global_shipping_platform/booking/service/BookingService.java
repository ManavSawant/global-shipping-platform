package com.MNS.global_shipping_platform.booking.service;

import com.MNS.global_shipping_platform.booking.repository.BookingRepository;
import com.MNS.global_shipping_platform.booking.dto.BookingRequest;
import com.MNS.global_shipping_platform.booking.dto.BookingResponse;
import com.MNS.global_shipping_platform.booking.BookingStatus;
import com.MNS.global_shipping_platform.booking.entity.Booking;
import com.MNS.global_shipping_platform.customer.entity.Customer;
import com.MNS.global_shipping_platform.customer.repository.CustomerRepository;
import com.MNS.global_shipping_platform.customer.CustomerNotFoundException;
import com.MNS.global_shipping_platform.exception.port.PortNotFoundException;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final PortRepository portRepository;

    public BookingResponse mapToResponseDTO(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getContactPersonFirstName()+ " "
                        + booking.getCustomer().getContactPersonLastName(),
                booking.getOriginPort().getPortName(),
                booking.getDestinationPort().getPortName(),
                booking.getCargoType(),
                booking.getCargoWeight(),
                booking.getNumberOfContainers(),
                booking.getStatus(),
                booking.getCreateDate()
                );
    }

    public BookingResponse createBooking(BookingRequest bookingRequest) {
        Customer customer = customerRepository.findById(bookingRequest.customerId())
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found with"));

        Port originPort = portRepository.findById(bookingRequest.originPortId())
                .orElseThrow(()-> new PortNotFoundException("Origin Port Not Found"));

        Port destinationPort = portRepository.findById(bookingRequest.destinationPortId())
                .orElseThrow(()-> new PortNotFoundException("Destination Port Not Found"));
        if(Objects.equals(originPort.getPortName(), destinationPort.getPortName())) {
            throw new PortNotFoundException("Origin Port and Destination Port are the same");
        }

        if(bookingRequest.cargoWeight() <= 0 ){
            throw new RuntimeException("Cargo weight must be greater than 0");
        }

        if (bookingRequest.numberOfContainers() <= 0) {
            throw new RuntimeException("Number of containers must be greater than 0");
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .cargoType(bookingRequest.cargoType())
                .numberOfContainers(bookingRequest.numberOfContainers())
                .cargoWeight(bookingRequest.cargoWeight())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }

    public BookingResponse getBookingsByBookingId(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new RuntimeException("Booking Not Found"));
        return mapToResponseDTO(booking);
    }
}

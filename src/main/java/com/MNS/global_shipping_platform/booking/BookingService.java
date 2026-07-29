package com.MNS.global_shipping_platform.booking;

import com.MNS.global_shipping_platform.customer.Customer;
import com.MNS.global_shipping_platform.customer.CustomerRepository;
import com.MNS.global_shipping_platform.exception.CustomerNotFoundException;
import com.MNS.global_shipping_platform.exception.PortNotFoundException;
import com.MNS.global_shipping_platform.port.Port;
import com.MNS.global_shipping_platform.port.PortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final PortRepository portRepository;

    public BookingResponseDto mapToResponseDTO(Booking booking) {
        return new BookingResponseDto(
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

    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        Customer customer = customerRepository.findById(bookingRequestDto.customerId())
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found with"));

        Port originPort = portRepository.findById(bookingRequestDto.originPortId())
                .orElseThrow(()-> new PortNotFoundException("Origin Port Not Found"));

        Port destinationPort = portRepository.findById(bookingRequestDto.destinationPortId())
                .orElseThrow(()-> new PortNotFoundException("Destination Port Not Found"));
        if(Objects.equals(originPort.getPortName(), destinationPort.getPortName())) {
            throw new PortNotFoundException("Origin Port and Destination Port are the same");
        }

        if(bookingRequestDto.cargoWeight() <= 0 ){
            throw new RuntimeException("Cargo weight must be greater than 0");
        }

        if (bookingRequestDto.numberOfContainers() <= 0) {
            throw new RuntimeException("Number of containers must be greater than 0");
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .cargoType(bookingRequestDto.cargoType())
                .numberOfContainers(bookingRequestDto.numberOfContainers())
                .cargoWeight(bookingRequestDto.cargoWeight())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }

    public BookingResponseDto getBookingsByBookingId(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new RuntimeException("Booking Not Found"));
        return mapToResponseDTO(booking);
    }
}

package com.MNS.global_shipping_platform.booking;

import com.MNS.global_shipping_platform.booking.dto.BookingResponse;
import com.MNS.global_shipping_platform.booking.entity.Booking;
import com.MNS.global_shipping_platform.booking.repository.BookingRepository;
import com.MNS.global_shipping_platform.booking.service.BookingService;
import com.MNS.global_shipping_platform.container.repository.ContainerRepository;
import com.MNS.global_shipping_platform.customer.entity.Customer;
import com.MNS.global_shipping_platform.customer.repository.CustomerRepository;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingException;
import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
import com.MNS.global_shipping_platform.sailing.SailingStatus;
import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import com.MNS.global_shipping_platform.sailing.repository.PortCallsRepository;
import com.MNS.global_shipping_platform.sailing.repository.SailingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PortRepository portRepository;

    @Mock
    private SailingRepository sailingRepository;

    @Mock
    private PortCallsRepository portCallsRepository;

    @Mock
    private ContainerRepository containerRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void approveBooking_shouldChangePendingToApproved(){
        //arrange
        UUID bookingId = UUID.randomUUID();

        Customer customer =  new Customer();
        customer.setContactPersonFirstName("Manav");
        customer.setContactPersonLastName("Sawant");

        Port originPort = new Port();
        originPort.setPortName("Mumbai Port");

        Port destinationPort = new Port();
        destinationPort.setPortName("Port of Singapore");

        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .cargoType("Electronics")
                .cargoWeight(12500)
                .numberOfContainers(2)
                .status(BookingStatus.PENDING)
                .build();

        when (bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        //act
        BookingResponse response = bookingService.approveBooking(bookingId);

        //assert
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        assertEquals(BookingStatus.APPROVED,response.bookingStatus());

        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBooking_shouldThrowException_whenBookingIsNotPending(){
        UUID bookingId = UUID.randomUUID();

        Customer customer =  new Customer();
        customer.setContactPersonFirstName("Manav");
        customer.setContactPersonLastName("Sawant");

        Port originPort = new Port();
        originPort.setPortName("Mumbai Port");

        Port destinationPort = new Port();
        destinationPort.setPortName("Port of Singapore");

        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .cargoType("Electronics")
                .cargoWeight(12500)
                .numberOfContainers(2)
                .status(BookingStatus.APPROVED)
                .build();

        when (bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(
                InvalidBookingException.class,
                ()->bookingService.approveBooking(bookingId)
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void cancelBooing(){

        UUID bookingId = UUID.randomUUID();

        Customer customer =  new Customer();
        customer.setContactPersonFirstName("Manav");
        customer.setContactPersonLastName("Sawant");

        Port originPort = new Port();
        originPort.setPortName("Mumbai Port");

        Port destinationPort = new Port();
        destinationPort.setPortName("Port of Singapore");

        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .cargoType("Electronics")
                .cargoWeight(12500)
                .numberOfContainers(2)
                .status(BookingStatus.APPROVED)
                .build();

        when (bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.cancelBooking(bookingId);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(BookingStatus.CANCELLED, response.bookingStatus());

        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_shouldThrowException_whenDeliveredBookingIsCancelled(){
        UUID bookingId = UUID.randomUUID();

        Customer customer =  new Customer();
        customer.setContactPersonFirstName("Manav");
        customer.setContactPersonLastName("Sawant");

        Port originPort = new Port();
        originPort.setPortName("Mumbai Port");

        Port destinationPort = new Port();
        destinationPort.setPortName("Port of Singapore");

        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .cargoType("Electronics")
                .cargoWeight(12500)
                .numberOfContainers(2)
                .status(BookingStatus.DELIVERED)
                .build();
        when (bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThrows(
                InvalidBookingException.class,
                () -> bookingService.cancelBooking(bookingId)
        );

        verify(bookingRepository, never()).save(booking);

    }

    @Test
    void assignSailingToBooking_shouldThrowException_whenBookingIsNotApproved() {

        UUID bookingId = UUID.randomUUID();
        UUID sailingId = UUID.randomUUID();

        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        assertThrows(
                InvalidBookingException.class,
                () -> bookingService.assignSailingToBooking(bookingId, sailingId)
        );

        verify(sailingRepository, never()).findById(sailingId);
    }
    @Test
    void assignSailingToBooking_shouldThrowException_whenSailingIsNotScheduled() {


        UUID bookingId = UUID.randomUUID();
        UUID sailingId = UUID.randomUUID();

        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.APPROVED)
                .build();

        Sailing sailing = Sailing.builder()
                .id(sailingId)
                .status(SailingStatus.PLANNED)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        when(sailingRepository.findById(sailingId))
                .thenReturn(Optional.of(sailing));

        assertThrows(
                InvalidSailingScheduleException.class,
                () -> bookingService.assignSailingToBooking(bookingId, sailingId)
        );

        verify(portCallsRepository, never())
                .findBySailingIdOrderBySequenceNumberAsc(sailingId);
    }

}

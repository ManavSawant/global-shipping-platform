package com.MNS.global_shipping_platform.booking;

import com.MNS.global_shipping_platform.booking.dto.BookingResponse;
import com.MNS.global_shipping_platform.booking.entity.Booking;
import com.MNS.global_shipping_platform.booking.repository.BookingRepository;
import com.MNS.global_shipping_platform.booking.service.BookingService;
import com.MNS.global_shipping_platform.booking.service.BookingStatusValidator;
import com.MNS.global_shipping_platform.booking.dto.BookingUpdateRequest;
import com.MNS.global_shipping_platform.container.ContainerStatus;
import com.MNS.global_shipping_platform.container.entity.Container;
import com.MNS.global_shipping_platform.container.repository.ContainerRepository;
import com.MNS.global_shipping_platform.customer.entity.Customer;
import com.MNS.global_shipping_platform.customer.repository.CustomerRepository;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingException;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingStatusException;
import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
import com.MNS.global_shipping_platform.sailing.entity.PortCall;
import com.MNS.global_shipping_platform.user.UserRole;
import com.MNS.global_shipping_platform.user.entity.User;
import com.MNS.global_shipping_platform.user.repository.UserRepository;
import com.MNS.global_shipping_platform.sailing.SailingStatus;
import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import com.MNS.global_shipping_platform.sailing.repository.PortCallsRepository;
import com.MNS.global_shipping_platform.sailing.repository.SailingRepository;
import com.MNS.global_shipping_platform.vessel.entity.Vessel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.springframework.security.access.AccessDeniedException;

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

    @Mock
    private UserRepository userRepository;

    @Spy
    private BookingStatusValidator bookingStatusValidator =
            new BookingStatusValidator();

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
        when(bookingRepository.save(booking)).thenReturn(booking);

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
                InvalidBookingStatusException.class,
                ()->bookingService.approveBooking(bookingId)
        );

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void cancelBooing(){

        UUID bookingId = UUID.randomUUID();
        User owner = customerUser(UUID.randomUUID());

        Customer customer =  new Customer();
        customer.setContactPersonFirstName("Manav");
        customer.setContactPersonLastName("Sawant");
        customer.setUser(owner);

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
        when(userRepository.findByEmail(owner.getEmail()))
                .thenReturn(Optional.of(owner));
        when(containerRepository.findByBookingId(bookingId))
                .thenReturn(java.util.List.of());
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponse response = bookingService.cancelBooking(
                bookingId, owner.getEmail());

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(BookingStatus.CANCELLED, response.bookingStatus());

        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_shouldThrowException_whenDeliveredBookingIsCancelled(){
        UUID bookingId = UUID.randomUUID();
        User owner = customerUser(UUID.randomUUID());

        Customer customer =  new Customer();
        customer.setContactPersonFirstName("Manav");
        customer.setContactPersonLastName("Sawant");
        customer.setUser(owner);

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
        when(userRepository.findByEmail(owner.getEmail()))
                .thenReturn(Optional.of(owner));

        assertThrows(
                InvalidBookingStatusException.class,
                () -> bookingService.cancelBooking(bookingId, owner.getEmail())
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

        verify(sailingRepository, never()).findByIdForUpdate(sailingId);
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

        when(sailingRepository.findByIdForUpdate(sailingId))
                .thenReturn(Optional.of(sailing));

        assertThrows(
                InvalidSailingScheduleException.class,
                () -> bookingService.assignSailingToBooking(bookingId, sailingId)
        );

        verify(portCallsRepository, never())
                .findBySailingIdOrderBySequenceNumberAsc(sailingId);
    }

    @Test
    void cancelBooking_shouldRejectAnotherCustomersBooking() {
        UUID bookingId = UUID.randomUUID();
        User owner = customerUser(UUID.randomUUID());
        User requester = customerUser(UUID.randomUUID());
        Customer customer = new Customer();
        customer.setUser(owner);

        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findByEmail(requester.getEmail()))
                .thenReturn(Optional.of(requester));

        assertThrows(AccessDeniedException.class,
                () -> bookingService.cancelBooking(bookingId, requester.getEmail()));

        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(containerRepository);
    }

    @Test
    void updateBooking_shouldRejectNonPositiveContainerCount() {
        UUID bookingId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        User owner = customerUser(ownerId);
        Customer customer = new Customer();
        customer.setUser(owner);
        Port origin = new Port();
        origin.setId(UUID.randomUUID());
        Port destination = new Port();
        destination.setId(UUID.randomUUID());
        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .originPort(origin)
                .destinationPort(destination)
                .status(BookingStatus.PENDING)
                .numberOfContainers(2)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findByEmail(owner.getEmail()))
                .thenReturn(Optional.of(owner));

        BookingUpdateRequest invalidWeight = new BookingUpdateRequest(
                null, null, null, -1, null);
        BookingUpdateRequest invalidContainerCount = new BookingUpdateRequest(
                null, null, null, null, 0);

        assertThrows(InvalidBookingException.class,
                () -> bookingService.updateBooking(
                        bookingId, invalidWeight, owner.getEmail()));
        assertThrows(InvalidBookingException.class,
                () -> bookingService.updateBooking(
                        bookingId, invalidContainerCount, owner.getEmail()));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void assignSailingToBooking_shouldIgnoreCancelledBookingCapacity() {
        UUID bookingId = UUID.randomUUID();
        UUID sailingId = UUID.randomUUID();

        Port origin = new Port();
        origin.setId(UUID.randomUUID());
        origin.setPortName("Origin");
        Port destination = new Port();
        destination.setId(UUID.randomUUID());
        destination.setPortName("Destination");

        Customer customer = new Customer();
        customer.setContactPersonFirstName("Test");
        customer.setContactPersonLastName("Customer");

        Sailing sailing = Sailing.builder()
                .id(sailingId)
                .sailingNumber("S-100")
                .status(SailingStatus.SCHEDULED)
                .vessel(Vessel.builder().capacityTeu(1).build())
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .originPort(origin)
                .destinationPort(destination)
                .cargoType("General")
                .cargoWeight(100)
                .containerSize(20)
                .numberOfContainers(1)
                .status(BookingStatus.APPROVED)
                .build();

        Booking cancelledBooking = Booking.builder()
                .status(BookingStatus.CANCELLED)
                .containerSize(20)
                .numberOfContainers(2)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(sailingRepository.findByIdForUpdate(sailingId))
                .thenReturn(Optional.of(sailing));
        when(portCallsRepository.findBySailingIdOrderBySequenceNumberAsc(sailingId))
                .thenReturn(List.of(
                        PortCall.builder().sailing(sailing).port(origin).sequenceNumber(1).build(),
                        PortCall.builder().sailing(sailing).port(destination).sequenceNumber(2).build()));
        when(bookingRepository.findBySailingId(sailingId))
                .thenReturn(List.of(cancelledBooking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponse response = bookingService.assignSailingToBooking(bookingId, sailingId);

        assertEquals(sailingId, response.sailingId());
        verify(sailingRepository).findByIdForUpdate(sailingId);
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_shouldRejectCancellationAfterCargoStartsMoving() {
        UUID bookingId = UUID.randomUUID();
        User owner = customerUser(UUID.randomUUID());
        Customer customer = new Customer();
        customer.setUser(owner);
        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .status(BookingStatus.APPROVED)
                .build();
        Container inTransit = Container.builder()
                .booking(booking)
                .containerStatus(ContainerStatus.IN_TRANSIT)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findByEmail(owner.getEmail()))
                .thenReturn(Optional.of(owner));
        when(containerRepository.findByBookingId(bookingId))
                .thenReturn(List.of(inTransit));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.cancelBooking(bookingId, owner.getEmail()));

        verify(bookingRepository, never()).save(any());
        verify(containerRepository, never()).saveAll(anyList());
    }

    @Test
    void cancelBooking_shouldReleaseAssignedAndLoadedContainers() {
        UUID bookingId = UUID.randomUUID();
        User owner = customerUser(UUID.randomUUID());
        Customer customer = new Customer();
        customer.setUser(owner);
        customer.setContactPersonFirstName("Test");
        customer.setContactPersonLastName("Customer");
        Port origin = new Port();
        origin.setPortName("Origin");
        Port destination = new Port();
        destination.setPortName("Destination");
        Booking booking = Booking.builder()
                .id(bookingId)
                .customer(customer)
                .originPort(origin)
                .destinationPort(destination)
                .status(BookingStatus.APPROVED)
                .build();
        Container assigned = Container.builder()
                .booking(booking)
                .containerStatus(ContainerStatus.ASSIGNED)
                .build();
        Container loaded = Container.builder()
                .booking(booking)
                .containerStatus(ContainerStatus.LOADED)
                .build();
        List<Container> containers = List.of(assigned, loaded);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(userRepository.findByEmail(owner.getEmail()))
                .thenReturn(Optional.of(owner));
        when(containerRepository.findByBookingId(bookingId))
                .thenReturn(containers);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.cancelBooking(bookingId, owner.getEmail());

        assertEquals(ContainerStatus.AVAILABLE, assigned.getContainerStatus());
        assertEquals(ContainerStatus.AVAILABLE, loaded.getContainerStatus());
        assertNull(assigned.getBooking());
        assertNull(loaded.getBooking());
        verify(containerRepository).saveAll(containers);
    }

    @Test
    void updateBookingStatusFromContainer_shouldWaitUntilAllContainersStartTransit() {
        Booking booking = bookingForStatusUpdate(BookingStatus.APPROVED, 2);
        List<Container> containers = List.of(
                containerWithStatus(ContainerStatus.IN_TRANSIT),
                containerWithStatus(ContainerStatus.LOADED));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(containerRepository.findByBookingId(booking.getId()))
                .thenReturn(containers);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponse response =
                bookingService.updateBookingStatusFromContainer(booking.getId());

        assertEquals(BookingStatus.APPROVED, response.bookingStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingStatusFromContainer_shouldAdvanceWhenAllContainersAreInTransit() {
        Booking booking = bookingForStatusUpdate(BookingStatus.APPROVED, 2);
        List<Container> containers = List.of(
                containerWithStatus(ContainerStatus.IN_TRANSIT),
                containerWithStatus(ContainerStatus.IN_TRANSIT));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(containerRepository.findByBookingId(booking.getId()))
                .thenReturn(containers);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponse response =
                bookingService.updateBookingStatusFromContainer(booking.getId());

        assertEquals(BookingStatus.IN_TRANSIT, response.bookingStatus());
    }

    @Test
    void updateBookingStatusFromContainer_shouldDeliverWhenAllContainersAreDelivered() {
        Booking booking = bookingForStatusUpdate(BookingStatus.IN_TRANSIT, 2);
        List<Container> containers = List.of(
                containerWithStatus(ContainerStatus.DELIVERED),
                containerWithStatus(ContainerStatus.DELIVERED));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(containerRepository.findByBookingId(booking.getId()))
                .thenReturn(containers);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponse response =
                bookingService.updateBookingStatusFromContainer(booking.getId());

        assertEquals(BookingStatus.DELIVERED, response.bookingStatus());
    }

    private Booking bookingForStatusUpdate(BookingStatus status, int containerCount) {
        Customer customer = new Customer();
        customer.setContactPersonFirstName("Test");
        customer.setContactPersonLastName("Customer");
        Port origin = new Port();
        origin.setPortName("Origin");
        Port destination = new Port();
        destination.setPortName("Destination");
        return Booking.builder()
                .id(UUID.randomUUID())
                .customer(customer)
                .originPort(origin)
                .destinationPort(destination)
                .cargoType("General")
                .cargoWeight(100)
                .numberOfContainers(containerCount)
                .containerSize(20)
                .status(status)
                .build();
    }

    private Container containerWithStatus(ContainerStatus status) {
        return Container.builder().containerStatus(status).build();
    }

    private User customerUser(UUID id) {
        return User.builder()
                .id(id)
                .email(id + "@example.com")
                .password("unused")
                .role(UserRole.CUSTOMER)
                .build();
    }

}

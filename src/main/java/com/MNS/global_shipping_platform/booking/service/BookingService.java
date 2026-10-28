package com.MNS.global_shipping_platform.booking.service;

import com.MNS.global_shipping_platform.booking.dto.BookingUpdateRequest;
import com.MNS.global_shipping_platform.booking.repository.BookingRepository;
import com.MNS.global_shipping_platform.booking.dto.BookingRequest;
import com.MNS.global_shipping_platform.booking.dto.BookingResponse;
import com.MNS.global_shipping_platform.booking.BookingStatus;
import com.MNS.global_shipping_platform.booking.entity.Booking;
import com.MNS.global_shipping_platform.container.entity.Container;
import com.MNS.global_shipping_platform.container.repository.ContainerRepository;
import com.MNS.global_shipping_platform.container.ContainerStatus;
import com.MNS.global_shipping_platform.customer.entity.Customer;
import com.MNS.global_shipping_platform.customer.repository.CustomerRepository;
import com.MNS.global_shipping_platform.exception.customer.CustomerNotFoundException;
import com.MNS.global_shipping_platform.exception.booking.BookingNotFoundException;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingException;
import com.MNS.global_shipping_platform.exception.port.PortNotFoundException;
import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNotFoundException;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
import com.MNS.global_shipping_platform.sailing.SailingStatus;
import com.MNS.global_shipping_platform.sailing.entity.PortCall;
import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import com.MNS.global_shipping_platform.sailing.repository.PortCallsRepository;
import com.MNS.global_shipping_platform.sailing.repository.SailingRepository;
import com.MNS.global_shipping_platform.user.UserRole;
import com.MNS.global_shipping_platform.user.entity.User;
import com.MNS.global_shipping_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final PortRepository portRepository;
    private final SailingRepository sailingRepository;
    private final PortCallsRepository portCallsRepository;
    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;
    private final BookingStatusValidator bookingStatusValidator;

    private BookingResponse mapToResponseDTO(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSailing() != null ? booking.getSailing().getId() : null,
                booking.getSailing() != null ? booking.getSailing().getSailingNumber() : null,
                booking.getCustomer().getContactPersonFirstName() + " " +
                booking.getCustomer().getContactPersonLastName() + " ",
                booking.getOriginPort().getPortName(),
                booking.getDestinationPort().getPortName(),
                booking.getCargoType(),
                booking.getCargoWeight(),
                booking.getNumberOfContainers(),
                booking.getStatus(),
                booking.getCreateDate()
        );
    }

    public int calculateBookingTeu(Booking booking){

        int teuPerContainer;
        if (booking.getContainerSize() == 20){
            teuPerContainer = 1;
        } else if (booking.getContainerSize() == 40) {
            teuPerContainer = 2;
        }else{
            throw new InvalidBookingException(
                    "Unsupported container size: " + booking.getContainerSize()
            );
        }

        return booking.getNumberOfContainers() * teuPerContainer;
    }

    public BookingResponse createBooking(BookingRequest bookingRequest, String email) {

        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer profile not found for user: " + email));

        Port originPort = portRepository.findById(bookingRequest.originPortId())
                .orElseThrow(()-> new PortNotFoundException("Origin Port Not Found"));

        Port destinationPort = portRepository.findById(bookingRequest.destinationPortId())
                .orElseThrow(()-> new PortNotFoundException("Destination Port Not Found"));
        if(originPort.getId().equals(destinationPort.getId())) {
            throw new PortNotFoundException("Origin Port and Destination Port are the same");
        }

        if(bookingRequest.cargoWeight() <= 0 ){
            throw new InvalidBookingException("Cargo weight must be greater than 0");
        }

        if (bookingRequest.numberOfContainers() <= 0) {
            throw new InvalidBookingException("Number of containers must be greater than 0");
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .cargoType(bookingRequest.cargoType())
                .numberOfContainers(bookingRequest.numberOfContainers())
                .containerSize(bookingRequest.containerSize())
                .cargoWeight(bookingRequest.cargoWeight())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }

    public BookingResponse getBookingsById(UUID bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new BookingNotFoundException("Booking Not Found with" + bookingId));

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with email" + email));

        if(user.getRole() == UserRole.CUSTOMER &&
                !booking.getCustomer().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Your are not allowed to access this booking");
        }
        return mapToResponseDTO(booking);
    }

    public List<BookingResponse> getAllBookings(){
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public void deleteBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking Not Found with" + bookingId));
        bookingRepository.delete(booking);
    }

    public BookingResponse updateBooking(UUID bookingId,
                                         BookingUpdateRequest updateRequest,
                                         String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new BookingNotFoundException("Booking Not Found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found with email" + email));

        if(!booking.getCustomer().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Your are not allowed to access this booking");
        }


        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingException("Only pending booking can be updated");
        }

        if (updateRequest.originPortId() != null) {
            Port originPort = portRepository.findById(updateRequest.originPortId())
                    .orElseThrow(()->new PortNotFoundException("Origin Port Not Found"));

            booking.setOriginPort(originPort);

        }

        if (updateRequest.destinationPortId() != null) {
            Port destinationPort = portRepository.findById(updateRequest.destinationPortId())
                    .orElseThrow(()->new PortNotFoundException("Destination Port Not Found"));

            booking.setDestinationPort(destinationPort);
        }

        if (booking.getOriginPort().getId().equals(booking.getDestinationPort().getId())) {
            throw new InvalidBookingException("Origin Port and Destination Port cannot be the same");
        }


        if (updateRequest.cargoType() != null) {
            booking.setCargoType(updateRequest.cargoType());
        }

        if (updateRequest.cargoWeight() != null) {
            booking.setCargoWeight(updateRequest.cargoWeight());
        }

        if (updateRequest.numberOfContainers() != null) {
            booking.setNumberOfContainers(updateRequest.numberOfContainers());
        }

        Booking updateBooking = bookingRepository.save(booking);
        return mapToResponseDTO(updateBooking);
    }

    public BookingResponse approveBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->
                        new BookingNotFoundException(
                                "Booking Not Found with" + bookingId));

        bookingStatusValidator.validateTransition(
                booking.getStatus(),
                BookingStatus.APPROVED
        );

        booking.setStatus(BookingStatus.APPROVED);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }

    public BookingResponse rejectBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new BookingNotFoundException("Booking Not Found with" + bookingId));

        bookingStatusValidator.validateTransition(
                booking.getStatus(),
                BookingStatus.REJECTED
        );

        booking.setStatus(BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }

    public BookingResponse cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->
                        new BookingNotFoundException(
                                "Booking Not Found with" + bookingId));

        bookingStatusValidator.validateTransition(
                booking.getStatus(),
                BookingStatus.CANCELLED
        );

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }

    @Transactional
    public BookingResponse assignSailingToBooking(UUID bookingId, UUID sailingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->
                        new BookingNotFoundException(
                                "Booking Not Found with" + bookingId));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new InvalidBookingException("Sailing can only be assigned to APPROVED booking");
        }

        if (booking.getSailing() != null) {
            throw new InvalidBookingException("Sailing is already assigned to this booking");
        }

        Sailing sailing = sailingRepository.findById(sailingId)
                .orElseThrow(()->
                        new SailingNotFoundException(
                                "Sailing Not Found with" + sailingId));

        if(sailing.getStatus() != SailingStatus.SCHEDULED){
            throw new InvalidSailingScheduleException(("Only SCHEDULED can be assigned to Booking"));
        }

        // 1. Validate booking route
        UUID originPortId = booking.getOriginPort().getId();
        UUID destinationPortId = booking.getDestinationPort().getId();

        List<PortCall> portCalls =
                portCallsRepository.findBySailingIdOrderBySequenceNumberAsc(sailingId);

        Integer originSequence = null;
        Integer destinationSequence = null;
        for(PortCall portCall : portCalls) {

            if(portCall.getPort().getId().equals(originPortId)) {
                originSequence = portCall.getSequenceNumber();
                break;
            }
        }

        if (originSequence == null) {
            throw new InvalidBookingException(
                    "Booking origin port is not part of the sailing route");
        }

        for(PortCall portCall : portCalls) {

            if(portCall.getSequenceNumber()> originSequence
                    && portCall.getPort().getId().equals(destinationPortId)) {

                destinationSequence = portCall.getSequenceNumber();
                break;
            }
        }

        if (destinationSequence == null) {
            throw new InvalidBookingException(
                    "Booking destination port is not reachable on this sailing");
        }

        // 2. Calculate already used TEU

        List<Booking> existingBooking =
                bookingRepository.findBySailingId(sailingId);

        int usedTeu = existingBooking.stream()
                .mapToInt(this::calculateBookingTeu)
                .sum();

        // 3. Calculate requested booking TEU by customer
        int requestedTeu = calculateBookingTeu(booking);

        // check the vessel capacity

        int vesselCapacity =
                sailing.getVessel().getCapacityTeu();

        if (usedTeu + requestedTeu >  vesselCapacity) {
            throw new InvalidBookingException(
                    "Sailing does not have enough TEU capacity. "
                            + "Available: "
                            + (vesselCapacity - usedTeu)
                            + "Requested TEU: "
                            +  requestedTeu
            );
        }

        booking.setSailing(sailing);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }

    public BookingResponse updateBookingStatusFromContainer(UUID bookingId){

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->
                        new BookingNotFoundException(
                                "Booking Not Found with" + bookingId));

        List<Container> containers =
                containerRepository.findByBookingId(bookingId);


        if (containers.size() < booking.getNumberOfContainers()) {
            return mapToResponseDTO(booking);
        }

        boolean isAllInTransit = containers.stream()
                .allMatch(container ->
                        container.getContainerStatus() == ContainerStatus.IN_TRANSIT);

        boolean isAllDelivered = containers.stream()
                .allMatch(container ->
                        container.getContainerStatus() == ContainerStatus.DELIVERED);

        if(isAllDelivered){
            bookingStatusValidator.validateTransition(
                    booking.getStatus(),
                    BookingStatus.CANCELLED
            );

            booking.setStatus(BookingStatus.DELIVERED);

        } else if (isAllInTransit) {
            bookingStatusValidator.validateTransition(
                    booking.getStatus(),
                    BookingStatus.IN_TRANSIT
            );

            booking.setStatus(BookingStatus.IN_TRANSIT);

        }

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDTO(savedBooking);
    }
}

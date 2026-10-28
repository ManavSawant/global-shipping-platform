package com.MNS.global_shipping_platform.container;

import com.MNS.global_shipping_platform.booking.entity.Booking;
import com.MNS.global_shipping_platform.booking.repository.BookingRepository;
import com.MNS.global_shipping_platform.booking.service.BookingService;
import com.MNS.global_shipping_platform.container.Dto.ContainerResponse;
import com.MNS.global_shipping_platform.container.entity.Container;
import com.MNS.global_shipping_platform.container.repository.ContainerRepository;
import com.MNS.global_shipping_platform.container.service.ContainerService;
import com.MNS.global_shipping_platform.customer.repository.CustomerRepository;
import com.MNS.global_shipping_platform.exception.container.InvalidContainerStatusException;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
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
public class ContainerServiceTest {

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
    private ContainerService containerService;

    @Mock
    private BookingService bookingService;

    @Test
    void loadContainer_shouldChangeAssignedToLoad(){
        UUID containerId = UUID.randomUUID();
        Container container = Container.builder()
                .id(containerId)
                .containerNumber("MNSU1000")
                .containerType(ContainerType.DRY_STORAGE)
                .size(20)
                .containerStatus(ContainerStatus.ASSIGNED)
                .build();

        when(containerRepository.findById(containerId))
                .thenReturn(Optional.of(container));

        ContainerResponse response = containerService.loadContainer(containerId);

        assertEquals(ContainerStatus.LOADED, container.getContainerStatus());
        assertEquals(ContainerStatus.LOADED, response.containerStatus());

        verify(containerRepository).save(container);
    }

    @Test
    void loadContainer_shouldThrowException_whenContainerIsNotAssigned(){

        UUID containerId = UUID.randomUUID();
        Container container = Container.builder()
                .id(containerId)
                .containerNumber("MNSU1000")
                .containerType(ContainerType.DRY_STORAGE)
                .size(20)
                .containerStatus(ContainerStatus.AVAILABLE)
                .build();

        when(containerRepository.findById(containerId))
                .thenReturn(Optional.of(container));

        assertThrows(InvalidContainerStatusException.class,
                ()->containerService.loadContainer(containerId));

        verify(containerRepository, never()).save(container);
    }

    @Test
    void startContainerTransit_shouldChangeLoadedToInitialState(){

        UUID containerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        Booking booking = Booking.builder()
                .id(bookingId)
                .build();

        Container container = Container.builder()
                .id(containerId)
                .containerNumber("MNSU1000")
                .containerType(ContainerType.DRY_STORAGE)
                .size(20)
                .containerStatus(ContainerStatus.LOADED)
                .build();

        when(containerRepository.findById(containerId))
                .thenReturn(Optional.of(container));

        ContainerResponse response = containerService.startContainerTransit(containerId);

        assertEquals(ContainerStatus.IN_TRANSIT, container.getContainerStatus());
        assertEquals(ContainerStatus.IN_TRANSIT, response.containerStatus());

        verify(containerRepository).save(container);
        verify(bookingService).updateBookingStatusFromContainer(bookingId);

    }

    @Test
    void startContainerTransit_shouldThrowException_whenContainerIsNotLoaded() {

        UUID containerId = UUID.randomUUID();

        Container container = Container.builder()
                .id(containerId)
                .containerNumber("MNSU100004")
                .containerType(ContainerType.DRY_STORAGE)
                .size(20)
                .containerStatus(ContainerStatus.AVAILABLE)
                .build();

        when(containerRepository.findById(containerId))
                .thenReturn(Optional.of(container));


        assertThrows(
                InvalidContainerStatusException.class,
                () -> containerService.startContainerTransit(containerId)
        );

        verify(containerRepository, never()).save(container);
        verify(bookingService, never())
                .updateBookingStatusFromContainer(any());
    }
    @Test
    void deliverContainer_shouldChangeInTransitToDelivered() {


        UUID containerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        Booking booking = Booking.builder()
                .id(bookingId)
                .build();

        Container container = Container.builder()
                .id(containerId)
                .containerNumber("MNSU100005")
                .containerType(ContainerType.DRY_STORAGE)
                .size(20)
                .containerStatus(ContainerStatus.IN_TRANSIT)
                .booking(booking)
                .build();

        when(containerRepository.findById(containerId))
                .thenReturn(Optional.of(container));


        ContainerResponse response =
                containerService.deliverContainer(containerId);

        assertEquals(
                ContainerStatus.DELIVERED,
                container.getContainerStatus()
        );

        assertEquals(
                ContainerStatus.DELIVERED,
                response.containerStatus()
        );

        verify(containerRepository).save(container);

        verify(bookingService)
                .updateBookingStatusFromContainer(bookingId);
    }

    @Test
    void deliverContainer_shouldThrowException_whenContainerIsNotInTransit() {


        UUID containerId = UUID.randomUUID();

        Container container = Container.builder()
                .id(containerId)
                .containerNumber("MNSU100006")
                .containerType(ContainerType.DRY_STORAGE)
                .size(20)
                .containerStatus(ContainerStatus.AVAILABLE)
                .build();

        when(containerRepository.findById(containerId))
                .thenReturn(Optional.of(container));


        assertThrows(
                InvalidContainerStatusException.class,
                () -> containerService.deliverContainer(containerId)
        );

        // Verify
        verify(containerRepository, never()).save(container);

        verify(bookingService, never())
                .updateBookingStatusFromContainer(any());
    }
}

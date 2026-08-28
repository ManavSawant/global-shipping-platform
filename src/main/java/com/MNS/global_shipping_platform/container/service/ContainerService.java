package com.MNS.global_shipping_platform.container.service;

import com.MNS.global_shipping_platform.booking.BookingStatus;
import com.MNS.global_shipping_platform.booking.entity.Booking;
import com.MNS.global_shipping_platform.booking.repository.BookingRepository;
import com.MNS.global_shipping_platform.booking.service.BookingService;
import com.MNS.global_shipping_platform.container.ContainerStatus;
import com.MNS.global_shipping_platform.container.Dto.ContainerRequest;
import com.MNS.global_shipping_platform.container.Dto.ContainerResponse;
import com.MNS.global_shipping_platform.container.entity.Container;
import com.MNS.global_shipping_platform.container.entity.ContainerUpdateRequest;
import com.MNS.global_shipping_platform.container.repository.ContainerRepository;
import com.MNS.global_shipping_platform.exception.booking.BookingNotFoundException;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingException;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingStatusException;
import com.MNS.global_shipping_platform.exception.container.ContainerAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.container.ContainerNotFoundException;
import com.MNS.global_shipping_platform.exception.container.InvalidContainerStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContainerService {
    private final ContainerRepository containerRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    private ContainerResponse mapToResponseDto(Container container) {
        return new ContainerResponse(
                container.getId(),
                container.getContainerType(),
                container.getContainerNumber(),
                container.getSize(),
                container.getWeight(),
                container.getContainerStatus()

        );
    }

    public ContainerResponse createContainer(ContainerRequest containerRequest) {

        if(containerRepository.existsByContainerNumber(containerRequest.containerNumber())){
            throw new ContainerAlreadyExistsException("Container already exists with this" + containerRequest.containerNumber());
        }

        Container newContainer = Container.builder()
                .containerNumber(containerRequest.containerNumber())
                .containerType(containerRequest.containerType())
                .size(containerRequest.size())
                .weight(containerRequest.weight())
                .containerStatus(ContainerStatus.AVAILABLE)
                .build();
        Container savedContainer = containerRepository.save(newContainer);
        return mapToResponseDto(savedContainer);
    }

    public ContainerResponse getContainerById(UUID  containerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(()-> new ContainerNotFoundException("Container not found with id" + containerId));

        return mapToResponseDto(container);
    }

    public List<ContainerResponse> getAllContainers() {
        return containerRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public List<ContainerResponse> getContainersByBookingId(UUID  bookingId) {
        //List<Container> containers = containerRepository.findByBookingId(bookingId);
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking Not Found with " + bookingId));

        return containerRepository.findByBookingId(bookingId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ContainerResponse updateContainer(UUID  containerId, ContainerUpdateRequest containerRequest) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(()-> new ContainerNotFoundException("Container not found with id" + containerId));

        if(container.getContainerStatus() != ContainerStatus.AVAILABLE){
            throw new InvalidContainerStatusException("Only AVAILABLE containers can be updated");
        }

        container.setContainerType(containerRequest.containerType());
        container.setSize(containerRequest.size());
        container.setWeight(containerRequest.weight());

        Container updatedContainer = containerRepository.save(container);
        return mapToResponseDto(updatedContainer);

    }

    public void deleteContainerById(UUID  containerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(()-> new ContainerNotFoundException("Container not found with id" + containerId));
        containerRepository.delete(container);
    }

    public ContainerResponse assignContainerToBooking(UUID  containerId, UUID  bookingId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(()-> new ContainerNotFoundException("Container not found with id" + containerId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking Not Found with " + bookingId));

        if(container.getContainerStatus() != ContainerStatus.AVAILABLE){
            throw new InvalidContainerStatusException("Only AVAILABLE containers can be assigned");
        }

        if(booking.getStatus() != BookingStatus.APPROVED){
            throw new InvalidBookingStatusException("Only APPROVED booking can be assigned to the container");
        }

        int assignedContainers = containerRepository.findByBookingId(bookingId).size();

        if (assignedContainers >= booking.getNumberOfContainers()) {
            throw new InvalidBookingException("Required number of containers are already assigned");
        }

        container.setContainerStatus(ContainerStatus.ASSIGNED);
        container.setBooking(booking);
        containerRepository.save(container);
        return mapToResponseDto(container);
    }

    public ContainerResponse loadContainer(UUID  containerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(()-> new ContainerNotFoundException("Container not found with id" + containerId));

        if(container.getContainerStatus() != ContainerStatus.ASSIGNED){
            throw new InvalidContainerStatusException("Only ASSIGNED containers can be loaded");
        }

        container.setContainerStatus(ContainerStatus.LOADED);
        containerRepository.save(container);
        return mapToResponseDto(container);
    }

    public ContainerResponse startContainerTransit(UUID containerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(()-> new ContainerNotFoundException("Container not found with id" + containerId));

        if(container.getContainerStatus() != ContainerStatus.LOADED){
            throw new InvalidContainerStatusException("Only LOADED containers can be set to transit");
        }

        container.setContainerStatus(ContainerStatus.IN_TRANSIT);
        containerRepository.save(container);

        bookingService.updateBookingStatusFromContainer(container.getBooking().getId());
        return mapToResponseDto(container);
    }

    public ContainerResponse deliverContainer(UUID  containerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(()-> new ContainerNotFoundException("Container not found with id" + containerId));

        if(container.getContainerStatus() != ContainerStatus.IN_TRANSIT){
            throw new InvalidContainerStatusException("Only IN_TRANSIT containers can be delivered");
        }
        container.setContainerStatus(ContainerStatus.DELIVERED);
        containerRepository.save(container);

        bookingService.updateBookingStatusFromContainer(container.getBooking().getId());
        return mapToResponseDto(container);
    }




}

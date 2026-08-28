package com.MNS.global_shipping_platform.container.controller;

import com.MNS.global_shipping_platform.container.Dto.ContainerRequest;
import com.MNS.global_shipping_platform.container.Dto.ContainerResponse;
import com.MNS.global_shipping_platform.container.service.ContainerService;
import com.MNS.global_shipping_platform.container.entity.ContainerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/containers")
@RequiredArgsConstructor
public class ContainerController {

    private final ContainerService containerService;


    @PostMapping
    public ContainerResponse createContainer(
            @RequestBody ContainerRequest request) {

        return containerService.createContainer(request);
    }


    @GetMapping("/{containerId}")
    public ContainerResponse getContainerById(
            @PathVariable UUID containerId) {

        return containerService.getContainerById(containerId);
    }


    @GetMapping
    public List<ContainerResponse> getAllContainers() {

        return containerService.getAllContainers();
    }


    @GetMapping("/booking/{bookingId}")
    public List<ContainerResponse> getContainersByBookingId(
            @PathVariable UUID bookingId) {

        return containerService.getContainersByBookingId(bookingId);
    }


    @PutMapping("/{containerId}")
    public ContainerResponse updateContainer(
            @PathVariable UUID containerId,
            @RequestBody ContainerUpdateRequest request) {

        return containerService.updateContainer(
                containerId,
                request
        );
    }


    @DeleteMapping("/{containerId}")
    public ResponseEntity<Void> deleteContainer(
            @PathVariable UUID containerId) {

        containerService.deleteContainerById(containerId);

        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{containerId}/assign-booking/{bookingId}")
    public ContainerResponse assignContainerToBooking(
            @PathVariable UUID containerId,
            @PathVariable UUID bookingId) {

        return containerService.assignContainerToBooking(
                containerId,
                bookingId
        );
    }


    @PatchMapping("/{containerId}/load")
    public ContainerResponse loadContainer(
            @PathVariable UUID containerId) {

        return containerService.loadContainer(containerId);
    }


    @PatchMapping("/{containerId}/start-transit")
    public ContainerResponse startContainerTransit(
            @PathVariable UUID containerId) {

        return containerService.startContainerTransit(containerId);
    }

    @PatchMapping("/{containerId}/deliver")
    public ContainerResponse deliverContainer(
            @PathVariable UUID containerId) {

        return containerService.deliverContainer(containerId);
    }
}

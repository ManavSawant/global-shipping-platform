package com.MNS.global_shipping_platform.port.controller;

import com.MNS.global_shipping_platform.port.dto.PortRequest;
import com.MNS.global_shipping_platform.port.dto.PortResponse;
import com.MNS.global_shipping_platform.port.service.PortService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ports")
public class PortController {

    private final PortService portService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<PortResponse> createPort(
            @Valid @RequestBody PortRequest requestDto) {

        PortResponse response = portService.createPort(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{portId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<PortResponse> getPortById(
            @PathVariable UUID portId) {

        PortResponse response = portService.getPortById(portId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<PortResponse>> getAllPorts() {

        List<PortResponse> response = portService.getAllPorts();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{portId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<PortResponse> updatePort(
            @PathVariable UUID portId,
            @Valid @RequestBody PortRequest requestDto) {

        PortResponse response =
                portService.updatePort(portId, requestDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{portId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePortById(
            @PathVariable UUID portId) {

        portService.deletePortById(portId);

        return ResponseEntity.noContent().build();
    }
}


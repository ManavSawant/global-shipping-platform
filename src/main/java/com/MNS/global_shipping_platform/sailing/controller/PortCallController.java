package com.MNS.global_shipping_platform.sailing.controller;

import com.MNS.global_shipping_platform.sailing.service.PortCallService;
import com.MNS.global_shipping_platform.sailing.dto.PortCallRequest;
import com.MNS.global_shipping_platform.sailing.dto.PortCallResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/port-calls")
public class PortCallController {

    private final PortCallService portCallService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public PortCallResponse createPortCall(
            @RequestBody PortCallRequest portCallRequest) {

        return portCallService.createPortCall(portCallRequest);
    }

    @GetMapping("/{sailingId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public List<PortCallResponse> getAllPortCallsBySailingId(
            @PathVariable UUID sailingId) {

        return portCallService.getAllPortCallsBySailingId(sailingId);
    }
}
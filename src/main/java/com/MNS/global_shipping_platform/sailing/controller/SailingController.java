package com.MNS.global_shipping_platform.sailing.controller;

import com.MNS.global_shipping_platform.sailing.dto.SailingRequest;
import com.MNS.global_shipping_platform.sailing.dto.SailingResponse;
import com.MNS.global_shipping_platform.sailing.dto.UpdateSailingRequest;
import com.MNS.global_shipping_platform.sailing.service.SailingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sailings")
@RequiredArgsConstructor
public class SailingController {

    private final SailingService sailingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResponseEntity<SailingResponse> createSailing(
            @Valid @RequestBody SailingRequest request) {

        SailingResponse response = sailingService.createSailing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResponseEntity<SailingResponse> getSailingById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                sailingService.getSailingById(id)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResponseEntity<List<SailingResponse>> getAllSailings() {

        return ResponseEntity.ok(
                sailingService.getAllSailings()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResponseEntity<SailingResponse> updateSailing(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSailingRequest request) {

        return ResponseEntity.ok(
                sailingService.updateSailing(id, request)
        );
    }

    @PatchMapping("/{sailingId}/assign-vessel/{vesselId}")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResponseEntity<SailingResponse> assignSailingToVessel(
            @PathVariable UUID sailingId,
            @PathVariable UUID vesselId) {

        return ResponseEntity.ok(
                sailingService.assignSailingToVessel(
                        sailingId,
                        vesselId
                )
        );
    }

    @PatchMapping("/{sailingId}/schedule")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResponseEntity<SailingResponse> scheduleSailing(
            @PathVariable UUID sailingId) {

        return ResponseEntity.ok(
                sailingService.scheduleSailing(sailingId)
        );
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public SailingResponse startSailing(@PathVariable UUID id) {

        return sailingService.startSailing(id);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public SailingResponse completeSailing(@PathVariable UUID id) {

        return sailingService.completeSailing(id);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public SailingResponse cancelSailing(@PathVariable UUID id) {

        return sailingService.cancelSailing(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSailing(
            @PathVariable UUID id) {

        sailingService.deleteSailing(id);

        return ResponseEntity.noContent().build();
    }
}

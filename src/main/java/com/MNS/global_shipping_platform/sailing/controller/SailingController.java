package com.MNS.global_shipping_platform.sailing.controller;

import com.MNS.global_shipping_platform.sailing.dto.SailingRequest;
import com.MNS.global_shipping_platform.sailing.dto.SailingResponse;
import com.MNS.global_shipping_platform.sailing.dto.UpdateSailingRequest;
import com.MNS.global_shipping_platform.sailing.service.SailingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sailings")
@RequiredArgsConstructor
public class SailingController {

    private final SailingService sailingService;

    @PostMapping
    public ResponseEntity<SailingResponse> createSailing(
            @Valid @RequestBody SailingRequest request) {

        SailingResponse response = sailingService.createSailing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SailingResponse> getSailingById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(sailingService.getSailingById(id));
    }

    @GetMapping
    public ResponseEntity<List<SailingResponse>> getAllSailings() {

        return ResponseEntity.ok(sailingService.getAllSailings());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SailingResponse> updateSailing(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSailingRequest request) {

        return ResponseEntity.ok(
                sailingService.updateSailing(id, request)
        );
    }

    @PatchMapping("/{sailingId}/assign-vessel/{vesselId}")
    public ResponseEntity<SailingResponse> assignSailingToVessel(
            @PathVariable UUID sailingId,
            @PathVariable UUID vesselId) {

        return ResponseEntity.ok(
                sailingService.assignSailingToVessel(sailingId, vesselId)
        );
    }

    @PatchMapping("/{sailingId}/schedule")
    public ResponseEntity<SailingResponse> scheduleSailing(
            @PathVariable UUID sailingId) {

        return ResponseEntity.ok(
                sailingService.scheduleSailing(sailingId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSailing(
            @PathVariable UUID id) {

        sailingService.deleteSailing(id);
        return ResponseEntity.noContent().build();
    }
}

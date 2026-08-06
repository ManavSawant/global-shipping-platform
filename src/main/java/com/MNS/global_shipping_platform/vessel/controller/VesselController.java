package com.MNS.global_shipping_platform.vessel.controller;

import com.MNS.global_shipping_platform.vessel.dto.VesselRequest;
import com.MNS.global_shipping_platform.vessel.dto.VesselResponse;
import com.MNS.global_shipping_platform.vessel.service.VesselService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vessels")
public class VesselController {

    private final VesselService vesselService;

    @PostMapping
    public ResponseEntity<VesselResponse> createVessel(@Valid @RequestBody VesselRequest vesselRequest) {
        VesselResponse responseDto = vesselService.createVessel(vesselRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VesselResponse> getVessel(@PathVariable UUID id) {
        return ResponseEntity.ok(vesselService.getVesselById(id));
    }

    @GetMapping
    public ResponseEntity<List<VesselResponse>> getAllVessel() {
        return ResponseEntity.ok(vesselService.getAllVessels());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VesselResponse> updateVessel(@PathVariable UUID id, @Valid @RequestBody VesselRequest vesselRequest) {
        return ResponseEntity.ok(vesselService.updateVessel(id, vesselRequest));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteVessel(@PathVariable UUID id) {
        vesselService.deleteVessel(id);
        return ResponseEntity.noContent().build();
    }
}
